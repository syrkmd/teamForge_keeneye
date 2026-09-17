ALTER TABLE notifications
    DROP CONSTRAINT ck_notifications_type;

ALTER TABLE notifications
    ADD CONSTRAINT ck_notifications_type CHECK (type IN (
        'INVITATION_RECEIVED', 'INVITATION_ACCEPTED', 'INVITATION_REJECTED',
        'TEAM_MEMBER_LEFT', 'TEAM_FORMATION_COMPLETED'
    ));
