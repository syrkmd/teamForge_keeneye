package org.yvl.teamforge.exception;

public class ProjectRoleHasNoSkillRequirementsException extends RuntimeException {
    public ProjectRoleHasNoSkillRequirementsException(Long projectRoleId) {
        super("Project role with id " + projectRoleId  + " has no skill requirements");
    }
}
