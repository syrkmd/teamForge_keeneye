package org.yvl.teamforge.matching.exception;

public class ProjectRoleHasNoSkillRequirementsException extends RuntimeException {
    public ProjectRoleHasNoSkillRequirementsException(Long projectRoleId) {
        super("Project role with id " + projectRoleId  + " has no skill requirements");
    }
}
