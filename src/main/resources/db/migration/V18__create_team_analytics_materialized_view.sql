CREATE MATERIALIZED VIEW team_analytics AS
WITH role_totals AS (
    SELECT t.id AS team_id,
           COUNT(pr.id) AS total_roles
    FROM teams t
    JOIN projects p ON p.id = t.project_id
    LEFT JOIN project_roles pr ON pr.project_id = p.id
    GROUP BY t.id
),
role_filled AS (
    SELECT t.id AS team_id,
           COUNT(pr.id) AS filled_roles
    FROM teams t
    JOIN projects p ON p.id = t.project_id
    JOIN project_roles pr ON pr.project_id = p.id
    WHERE EXISTS (
        SELECT 1
        FROM team_members tm
        WHERE tm.team_id = t.id
          AND tm.project_role_id = pr.id
          AND tm.status = 'ACTIVE'
    )
    GROUP BY t.id
),
skill_totals AS (
    SELECT t.id AS team_id,
           COUNT(prs.id) AS total_skills
    FROM teams t
    JOIN projects p ON p.id = t.project_id
    LEFT JOIN project_roles pr ON pr.project_id = p.id
    LEFT JOIN project_role_skills prs ON prs.project_role_id = pr.id
    GROUP BY t.id
),
skill_satisfied AS (
    SELECT t.id AS team_id,
           COUNT(prs.id) AS satisfied_skills
    FROM teams t
    JOIN projects p ON p.id = t.project_id
    JOIN project_roles pr ON pr.project_id = p.id
    JOIN project_role_skills prs ON prs.project_role_id = pr.id
    WHERE EXISTS (
        SELECT 1
        FROM team_members tm
        JOIN user_skills us ON us.user_id = tm.user_id
        WHERE tm.team_id = t.id
          AND tm.project_role_id = pr.id
          AND tm.status = 'ACTIVE'
          AND us.skill_id = prs.skill_id
          AND us.level >= prs.min_level
    )
    GROUP BY t.id
),
metrics AS (
    SELECT
        t.id AS team_id,
        COALESCE(rf.filled_roles, 0)::numeric / NULLIF(rt.total_roles, 0) * 100 AS role_fill_rate,
        COALESCE(ss.satisfied_skills, 0)::numeric / NULLIF(st.total_skills, 0) * 100 AS skill_coverage
    FROM teams t
    JOIN role_totals rt ON rt.team_id = t.id
    LEFT JOIN role_filled rf ON rf.team_id = t.id
    JOIN skill_totals st ON st.team_id = t.id
    LEFT JOIN skill_satisfied ss ON ss.team_id = t.id
),
health AS (
    SELECT
        team_id,
        COALESCE(skill_coverage, 0) AS skill_coverage,
        (COALESCE(role_fill_rate, 0) + COALESCE(skill_coverage, 0)) / 2 AS team_health
    FROM metrics
)
SELECT
    h.team_id,
    ROUND(h.team_health, 2) AS team_health,
    ROUND(h.skill_coverage, 2) AS skill_coverage,
    CASE
        WHEN h.team_health >= 80 THEN 'LOW'
        WHEN h.team_health >= 50 THEN 'MEDIUM'
        ELSE 'HIGH'
    END AS risk_level
FROM health h;

CREATE UNIQUE INDEX ux_team_analytics_team_id
    ON team_analytics (team_id);
