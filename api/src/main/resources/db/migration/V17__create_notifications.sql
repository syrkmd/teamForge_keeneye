CREATE TABLE notifications (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type                    VARCHAR(50) NOT NULL,
    title                   VARCHAR(255) NOT NULL,
    message                 TEXT NOT NULL,
    is_read                 BOOLEAN NOT NULL,
    created_at              TIMESTAMPTZ NOT NULL,
    user_id                 BIGINT NOT NULL,
    related_invitation_id   BIGINT,
    related_team_id         BIGINT,
    CONSTRAINT ck_notifications_type CHECK (type IN (
        'INVITATION_RECEIVED', 'INVITATION_ACCEPTED', 'INVITATION_REJECTED',
        'TEAM_MEMBER_LEFT', 'TEAM_MATCHING_COMPLETED'
    )),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notifications_invitation FOREIGN KEY (related_invitation_id) REFERENCES invitations (id),
    CONSTRAINT fk_notifications_team FOREIGN KEY (related_team_id) REFERENCES teams (id)
);
