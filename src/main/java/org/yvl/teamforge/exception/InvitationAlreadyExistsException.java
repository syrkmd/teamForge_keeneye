package org.yvl.teamforge.exception;

public class InvitationAlreadyExistsException extends RuntimeException {

    public InvitationAlreadyExistsException() {
        super("Pending invitation already exists");
    }
}