CREATE TABLE invitations (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status           VARCHAR(50) NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    responded_at     TIMESTAMPTZ,
    project_id       BIGINT NOT NULL,
    project_role_id  BIGINT NOT NULL,
    user_id          BIGINT NOT NULL,
    CONSTRAINT ck_invitations_status CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
    CONSTRAINT fk_invitations_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_invitations_project_role FOREIGN KEY (project_role_id) REFERENCES project_roles (id),
    CONSTRAINT fk_invitations_user FOREIGN KEY (user_id) REFERENCES users (id)
);
