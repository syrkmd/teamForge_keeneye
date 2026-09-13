package org.yvl.teamforge.skill.exception;

import org.yvl.teamforge.entity.enums.SkillCategoryName;

public class SkillAlreadyExistsException extends RuntimeException {

    public SkillAlreadyExistsException(SkillCategoryName category, String name) {
        super("Skill '" + name + "' already exists in category '" + category + "'");
    }
}