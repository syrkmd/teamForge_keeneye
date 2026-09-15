ALTER TABLE project_role_skills
    DROP CONSTRAINT fk_project_role_skills_project_role;

ALTER TABLE project_role_skills
    ADD CONSTRAINT fk_project_role_skills_project_role FOREIGN KEY (project_role_id) REFERENCES project_roles (id) ON DELETE CASCADE;

ALTER TABLE invitations
    DROP CONSTRAINT fk_invitations_project_role;

ALTER TABLE invitations
    ADD CONSTRAINT fk_invitations_project_role FOREIGN KEY (project_role_id) REFERENCES project_roles (id) ON DELETE CASCADE;
