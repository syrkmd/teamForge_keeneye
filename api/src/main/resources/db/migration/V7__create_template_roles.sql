CREATE TABLE template_roles (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name      VARCHAR(255) NOT NULL,
    description    TEXT NOT NULL,
    required_count INTEGER NOT NULL,
    template_id    BIGINT NOT NULL,
    CONSTRAINT fk_template_roles_template FOREIGN KEY (template_id) REFERENCES project_templates (id)
);
