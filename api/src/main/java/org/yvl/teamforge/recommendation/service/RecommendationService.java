package org.yvl.teamforge.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.Recommendation;
import org.yvl.teamforge.entity.Team;
import org.yvl.teamforge.team.exception.TeamNotFoundException;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.recommendation.dto.response.RecommendationView;
import org.yvl.teamforge.recommendation.mapper.RecommendationMapper;
import org.yvl.teamforge.repository.RecommendationRepository;
import org.yvl.teamforge.repository.TeamRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final TeamRepository teamRepository;
    private final RecommendationMapper recommendationMapper;
    private final ProjectAccessService projectAccessService;

    public List<RecommendationView> getRecommendations(
            UserPrincipal userPrincipal,
            Long projectId
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        Team team = teamRepository.findByProjectId(projectId).orElseThrow(() ->
                new TeamNotFoundException(projectId));

        List<Recommendation> recommendations = recommendationRepository.findByTeamIdOrderByCreatedAtDesc(team.getId());

        return recommendations.stream().map(recommendationMapper::toRecommendationView).toList();
    }
}
