package org.yvl.teamforge.skill.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.yvl.teamforge.entity.enums.SkillCategoryName;
import org.yvl.teamforge.validation.annotation.ValidUserSkillRequest;

@ValidUserSkillRequest
@Data
public class UserSkillRequest {

    private Long skillId;
    private String skillName;
    private SkillCategoryName category;

    @Min(1)
    @Max(5)
    @NotNull
    private Integer level;
}
