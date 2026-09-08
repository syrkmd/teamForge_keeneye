package org.yvl.teamforge.exception;

public class TeamMemberNotFoundException extends RuntimeException {
    public TeamMemberNotFoundException(Long teamMemberId) {
        super("TeamMember with id " + teamMemberId + " not found");
    }
}
