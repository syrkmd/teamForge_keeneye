package org.yvl.teamforge.exception;

public class ProjectRoleSkillAlreadyExistsException extends RuntimeException {
    public ProjectRoleSkillAlreadyExistsException(Long projectRoleId, Long skillId) {
        super("Skill with id: " + skillId + " is already added to project role with id: " + projectRoleId);
    }
}