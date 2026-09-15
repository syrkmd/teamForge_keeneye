package org.yvl.teamforge.skill.exception;

public class UserSkillAlreadyExistsException extends RuntimeException {
    public UserSkillAlreadyExistsException(Long skillId) {
        super("Skill with id " + skillId + " already exists");
    }
}
