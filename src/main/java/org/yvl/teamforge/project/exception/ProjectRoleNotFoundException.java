package org.yvl.teamforge.project.exception;

public class ProjectRoleNotFoundException extends RuntimeException {
    public ProjectRoleNotFoundException(Long roleId) {
        super("Project role with id " + roleId + " not found");
    }
}
