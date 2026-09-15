package org.yvl.teamforge.project.exception;

public class ProjectRoleNotBelongToProjectException extends RuntimeException {
    public ProjectRoleNotBelongToProjectException(Long roleId, Long projectId) {
        super("Project role with id: " + roleId + "does not belong to project with id: " + projectId);
    }
}
