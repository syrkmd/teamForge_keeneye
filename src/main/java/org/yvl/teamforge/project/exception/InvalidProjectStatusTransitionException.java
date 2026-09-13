package org.yvl.teamforge.project.exception;

public class InvalidProjectStatusTransitionException extends RuntimeException {
    public InvalidProjectStatusTransitionException() {
        super("Project cannot be archived from its current status");
    }
}
