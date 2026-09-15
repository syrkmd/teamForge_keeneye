CREATE TABLE team_members (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status           VARCHAR(50) NOT NULL,
    reason           TEXT,
    joined_at        TIMESTAMPTZ NOT NULL,
    left_at          TIMESTAMPTZ,
    team_id          BIGINT NOT NULL,
    user_id          BIGINT NOT NULL,
    project_role_id  BIGINT NOT NULL,
    CONSTRAINT ck_team_members_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_team_members_project_role FOREIGN KEY (project_role_id) REFERENCES project_roles (id)
);
