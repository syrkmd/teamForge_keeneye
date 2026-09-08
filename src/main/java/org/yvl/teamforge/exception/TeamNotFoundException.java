package org.yvl.teamforge.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long projectId) {
        super("Team not found with project id " + projectId);
    }
}
