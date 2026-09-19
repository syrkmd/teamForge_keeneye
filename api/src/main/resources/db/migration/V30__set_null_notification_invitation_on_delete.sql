ALTER TABLE notifications
    DROP CONSTRAINT fk_notifications_invitation;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_invitation
    FOREIGN KEY (related_invitation_id)
    REFERENCES invitations (id)
    ON DELETE SET NULL;
