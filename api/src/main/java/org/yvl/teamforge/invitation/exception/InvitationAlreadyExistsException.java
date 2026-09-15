package org.yvl.teamforge.invitation.exception;

public class InvitationAlreadyExistsException extends RuntimeException {

    public InvitationAlreadyExistsException() {
        super("Pending invitation already exists");
    }
}