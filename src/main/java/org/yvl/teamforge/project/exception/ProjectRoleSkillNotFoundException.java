package org.yvl.teamforge.project.exception;

public class ProjectRoleSkillNotFoundException extends RuntimeException {
    public ProjectRoleSkillNotFoundException(Long projectRoleId, Long skillId) {
        super("Skill with id: " + skillId + " is not required for project role with id: " + projectRoleId);
    }
}
