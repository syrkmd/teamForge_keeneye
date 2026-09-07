package org.yvl.teamforge.matching.mapper;

import org.mapstruct.Mapper;
import org.yvl.teamforge.matching.dto.response.CandidateView;
import org.yvl.teamforge.matching.dto.response.MatchedSkillView;
import org.yvl.teamforge.matching.dto.response.ProjectRoleCandidatesView;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchingMapper {

    MatchedSkillView toMatchedSkillView(
            Long skillId,
            String skillName,
            Integer userLevel,
            Integer minLevel
    );

    CandidateView toCandidateView(
            Long userId,
            Double averageRating,
            Double completionRate,
            List<MatchedSkillView> matchedSkills
    );

    ProjectRoleCandidatesView toProjectRoleCandidatesView(
            Long roleId,
            Integer requiredCount,
            Boolean enoughCandidates,
            List<CandidateView> candidates
    );
}
