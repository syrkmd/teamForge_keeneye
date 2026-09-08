package org.yvl.teamforge.exception;

public class TeamMemberAccessDeniedException extends RuntimeException {
    public TeamMemberAccessDeniedException() {
        super("You do not have access to this team member");
    }
}
