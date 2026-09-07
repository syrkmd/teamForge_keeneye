package org.yvl.teamforge.matching.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProjectRoleCandidatesView {

    private Long roleId;
    private Integer requiredCount;
    private Boolean enoughCandidates;
    private List<CandidateView> candidates;
}
