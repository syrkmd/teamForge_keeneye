package org.yvl.teamforge.matching.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CandidateView {

    private Long userId;
    private Double averageRating;
    private Double completionRate;
    private List<MatchedSkillView> matchedSkills;
}
