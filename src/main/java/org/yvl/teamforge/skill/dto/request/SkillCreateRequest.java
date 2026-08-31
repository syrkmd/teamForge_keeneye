package org.yvl.teamforge.skill.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.yvl.teamforge.entity.enums.SkillCategoryName;

@Data
public class SkillCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private SkillCategoryName category;
}
