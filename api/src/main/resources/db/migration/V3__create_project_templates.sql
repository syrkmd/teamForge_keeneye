CREATE TABLE project_templates (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_active   BOOLEAN NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL
);
