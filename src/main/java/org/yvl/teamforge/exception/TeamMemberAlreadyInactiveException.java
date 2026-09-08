package org.yvl.teamforge.exception;

public class TeamMemberAlreadyInactiveException extends RuntimeException {
    public TeamMemberAlreadyInactiveException() {
        super("Team member is already inactive");
    }
}
