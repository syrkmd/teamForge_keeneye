CREATE TABLE project_roles (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name      VARCHAR(255) NOT NULL,
    description    TEXT NOT NULL,
    required_count INTEGER NOT NULL,
    status         VARCHAR(50) NOT NULL,
    project_id     BIGINT NOT NULL,
    CONSTRAINT ck_project_roles_status CHECK (status IN ('OPEN', 'CLOSED')),
    CONSTRAINT fk_project_roles_project FOREIGN KEY (project_id) REFERENCES projects (id)
);
