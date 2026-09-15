package org.yvl.teamforge.skill.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.SkillCategoryName;
import org.yvl.teamforge.entity.enums.TypeSkill;

@Data
public class UserSkillView {

    private Long skillId;

    private String skillName;

    private SkillCategoryName category;

    private TypeSkill type;

    private Integer level;
}
