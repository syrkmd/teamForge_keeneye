package org.yvl.teamforge.project.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.SkillCategoryName;
import org.yvl.teamforge.entity.enums.TypeSkill;

@Data
public class ProjectRoleSkillView {

    private Long skillId;
    private String skillName;
    private SkillCategoryName category;
    private TypeSkill type;
    private Integer minLevel;
}
