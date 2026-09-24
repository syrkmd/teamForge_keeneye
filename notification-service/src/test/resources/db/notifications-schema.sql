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
        'TEAM_MEMBER_LEFT', 'TEAM_FORMATION_COMPLETED'
    ))
);
