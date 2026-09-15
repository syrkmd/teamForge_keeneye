package org.yvl.teamforge.invitation.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(Long invitationId) {
        super("Invitation id " + invitationId + " not found");
    }
}
