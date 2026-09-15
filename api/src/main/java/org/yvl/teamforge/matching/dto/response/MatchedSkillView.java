package org.yvl.teamforge.matching.dto.response;

import lombok.Data;

@Data
public class MatchedSkillView {

    private Long skillId;
    private String skillName;
    private Integer userLevel;
    private Integer minLevel;
}
