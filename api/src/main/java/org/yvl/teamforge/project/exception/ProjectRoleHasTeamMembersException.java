package org.yvl.teamforge.project.exception;

public class ProjectRoleHasTeamMembersException extends RuntimeException {
    public ProjectRoleHasTeamMembersException(Long projectRoleId) {
        super("Project role with id " + projectRoleId + " has team members and cannot be deleted");
    }
}
