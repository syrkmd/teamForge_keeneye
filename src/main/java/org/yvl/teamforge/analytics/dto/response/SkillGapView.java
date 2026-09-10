package org.yvl.teamforge.analytics.dto.response;

import lombok.Data;

@Data
public class SkillGapView {

    private String roleName;
    private String skillName;
    private Integer minLevel;
    private Integer actualLevel;
}
