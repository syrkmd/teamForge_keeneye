package org.yvl.teamforge.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(Long invitationId) {
        super("Invitation id " + invitationId + " not found");
    }
}
