package org.yvl.teamforge.skill.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.SkillCategoryName;
import org.yvl.teamforge.entity.enums.TypeSkill;

@Data
public class SkillView {
    private Long id;
    private String name;
    private TypeSkill type;
    private SkillCategoryName category;
}
