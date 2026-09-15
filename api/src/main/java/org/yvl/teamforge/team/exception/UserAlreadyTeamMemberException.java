package org.yvl.teamforge.team.exception;

public class UserAlreadyTeamMemberException extends RuntimeException {
    public UserAlreadyTeamMemberException() {
        super("User is already an active team member");
    }
}
