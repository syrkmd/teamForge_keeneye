package org.yvl.teamforge.exception;

public class SkillNotFoundException extends RuntimeException {
    public SkillNotFoundException(Long skillId) {
        super("Skill with id " + skillId + " not found");
    }
}
