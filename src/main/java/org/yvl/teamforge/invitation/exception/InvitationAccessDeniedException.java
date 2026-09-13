package org.yvl.teamforge.invitation.exception;

public class InvitationAccessDeniedException extends RuntimeException {
    public InvitationAccessDeniedException() {
        super("You do not have access to this invitation");
    }
}
