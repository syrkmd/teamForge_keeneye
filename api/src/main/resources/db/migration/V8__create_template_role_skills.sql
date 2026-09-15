CREATE TABLE template_role_skills (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    min_level         INTEGER NOT NULL,
    template_role_id  BIGINT NOT NULL,
    skill_id          BIGINT NOT NULL,
    CONSTRAINT ck_template_role_skills_min_level CHECK (min_level BETWEEN 1 AND 5),
    CONSTRAINT uk_template_role_skill UNIQUE (template_role_id, skill_id),
    CONSTRAINT fk_template_role_skills_template_role FOREIGN KEY (template_role_id) REFERENCES template_roles (id),
    CONSTRAINT fk_template_role_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
);
