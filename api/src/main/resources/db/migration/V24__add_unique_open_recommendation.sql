CREATE UNIQUE INDEX ux_recommendations_team_open
    ON recommendations (team_id)
    WHERE status = 'OPEN';