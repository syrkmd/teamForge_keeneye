package org.yvl.teamforge.invitation.exception;

public class InvitationAlreadyRespondedException extends RuntimeException {
    public InvitationAlreadyRespondedException() {
        super("Invitation has already been responded to");
    }
}
