CREATE TABLE projects (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status      VARCHAR(50) NOT NULL,
    deadline    TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    template_id BIGINT NOT NULL,
    owner_id    BIGINT NOT NULL,
    CONSTRAINT ck_projects_status CHECK (status IN ('DRAFT', 'RECRUITING', 'ACTIVE', 'ARCHIVED')),
    CONSTRAINT fk_projects_template FOREIGN KEY (template_id) REFERENCES project_templates (id),
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);
