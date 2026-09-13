package org.yvl.teamforge.skill.exception;

import org.yvl.teamforge.entity.enums.SkillCategoryName;

public class SkillCategoryNotFoundException extends RuntimeException {

    public SkillCategoryNotFoundException(SkillCategoryName categoryName) {
        super("Skill category not found: " + categoryName);
    }
}
