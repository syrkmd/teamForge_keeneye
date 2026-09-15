package org.yvl.teamforge.skill.exception;

public class UserSkillNotFoundException extends RuntimeException {
    public UserSkillNotFoundException(Long skillId) {
        super("User skill with id " + skillId + " not found");
    }
}
