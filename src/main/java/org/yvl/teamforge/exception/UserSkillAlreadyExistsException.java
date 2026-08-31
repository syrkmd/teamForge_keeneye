package org.yvl.teamforge.exception;

public class UserSkillAlreadyExistsException extends RuntimeException {
    public UserSkillAlreadyExistsException(Long skillId) {
        super("Skill with id " + skillId + " already exists");
    }
}
