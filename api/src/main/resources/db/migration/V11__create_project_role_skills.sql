CREATE TABLE project_role_skills (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    min_level        INTEGER NOT NULL,
    project_role_id  BIGINT NOT NULL,
    skill_id         BIGINT NOT NULL,
    CONSTRAINT ck_project_role_skills_min_level CHECK (min_level BETWEEN 1 AND 5),
    CONSTRAINT uk_project_role_skill UNIQUE (project_role_id, skill_id),
    CONSTRAINT fk_project_role_skills_project_role FOREIGN KEY (project_role_id) REFERENCES project_roles (id),
    CONSTRAINT fk_project_role_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
);
