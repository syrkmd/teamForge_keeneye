CREATE TABLE teams (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status     VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    project_id BIGINT NOT NULL,
    CONSTRAINT ck_teams_status CHECK (status IN ('FORMING', 'COMPLETED')),
    CONSTRAINT uk_teams_project UNIQUE (project_id),
    CONSTRAINT fk_teams_project FOREIGN KEY (project_id) REFERENCES projects (id)
);
