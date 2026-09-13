CREATE UNIQUE INDEX ux_team_members_active
    ON team_members (team_id, user_id, project_role_id)
    WHERE status = 'ACTIVE';
