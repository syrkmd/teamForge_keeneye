ALTER TABLE user_skills
    DROP CONSTRAINT fk_user_skills_skill;

ALTER TABLE user_skills
    ADD CONSTRAINT fk_user_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE;

ALTER TABLE project_role_skills
    DROP CONSTRAINT fk_project_role_skills_skill;

ALTER TABLE project_role_skills
    ADD CONSTRAINT fk_project_role_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE;

ALTER TABLE template_role_skills
    DROP CONSTRAINT fk_template_role_skills_skill;

ALTER TABLE template_role_skills
    ADD CONSTRAINT fk_template_role_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE;
