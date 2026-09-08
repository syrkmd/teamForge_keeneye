package org.yvl.teamforge.exception;

public class UserAlreadyTeamMemberException extends RuntimeException {
    public UserAlreadyTeamMemberException() {
        super("User is already an active team member");
    }
}
