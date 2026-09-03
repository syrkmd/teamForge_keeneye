package org.yvl.teamforge.exception;

public class InvalidProjectStatusTransitionException extends RuntimeException {
    public InvalidProjectStatusTransitionException() {
        super("Project cannot be archived from its current status");
    }
}
