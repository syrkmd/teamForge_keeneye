package org.yvl.teamforge.project.exception;

public class ProjectRoleNotOpenException extends RuntimeException {
    public ProjectRoleNotOpenException(Long projectRoleId) {
        super("Project role with id " + projectRoleId + " is not open for matching");
    }
}
