package org.yvl.teamforge.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.analytics.dto.projection.SkillGapRow;
import org.yvl.teamforge.entity.Recommendation;
import org.yvl.teamforge.entity.Team;
import org.yvl.teamforge.entity.TeamAnalyticsMaterializedView;
import org.yvl.teamforge.entity.enums.RecommendationPriority;
import org.yvl.teamforge.entity.enums.RecommendationStatus;
import org.yvl.teamforge.entity.enums.RiskLevel;
import org.yvl.teamforge.recommendation.mapper.RecommendationMapper;
import org.yvl.teamforge.repository.ProjectRoleSkillRepository;
import org.yvl.teamforge.repository.RecommendationRepository;
import org.yvl.teamforge.repository.TeamAnalyticsRepository;
import org.yvl.teamforge.repository.TeamRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationGenerationService {

    private final RecommendationRepository recommendationRepository;
    private final TeamRepository teamRepository;
    private final TeamAnalyticsRepository teamAnalyticsRepository;
    private final ProjectRoleSkillRepository projectRoleSkillRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationTextService textService;

    public void generateOrUpdate(
            Long teamId
    ) {
        log.info(
                "Recommendation generation started: teamId={}",
                teamId)
        ;

        Optional<Team> optionalTeam = teamRepository.findById(teamId);

        if (optionalTeam.isEmpty()) {
            log.warn(
                    "Recommendation generation skipped: team not found, teamId={}",
                    teamId
            );
            return;
        }

        Team team = optionalTeam.get();

        Optional<TeamAnalyticsMaterializedView> optionalTeamAnalytics = teamAnalyticsRepository.findByTeamId(teamId);

        if (optionalTeamAnalytics.isEmpty()) {
            log.warn(
                    "Recommendation generation skipped: analytics not found, teamId={}",
                    teamId
            );
            return;
        }

        TeamAnalyticsMaterializedView teamAnalytics = optionalTeamAnalytics.get();

        List<SkillGapRow> skillGap = projectRoleSkillRepository.findSkillGapRowsByProjectId(team.getProject().getId());

        boolean problemExists = !(teamAnalytics.getRiskLevel() == RiskLevel.LOW && skillGap.isEmpty());

        Optional<Recommendation> optionalRecommendation = recommendationRepository.findByTeamIdAndStatus(teamId, RecommendationStatus.OPEN);

        String titleRecommendation = textService.generateTitle(teamAnalytics.getRiskLevel(), skillGap);
        String descriptionRecommendation = textService.generateDescription(teamAnalytics.getRiskLevel(), skillGap);
        RecommendationPriority recommendationPriority = recommendationMapper.toRecommendationPriority(teamAnalytics.getRiskLevel());

        if (problemExists) {
            if (optionalRecommendation.isPresent()) {
                Recommendation recommendation = optionalRecommendation.get();

                recommendation.setTitle(titleRecommendation);
                recommendation.setDescription(descriptionRecommendation);
                recommendation.setPriority(recommendationPriority);

                log.info(
                        "Recommendation updated: teamId={}, recommendationId={}, priority={}",
                        teamId,
                        recommendation.getId(),
                        recommendationPriority
                );
            } else {
                Recommendation recommendation = recommendationRepository.save(
                        Recommendation.builder()
                                .team(team)
                                .title(titleRecommendation)
                                .description(descriptionRecommendation)
                                .priority(recommendationPriority)
                                .status(RecommendationStatus.OPEN)
                                .createdAt(Instant.now())
                                .build()
                );

                log.info(
                        "Recommendation created: teamId={}, recommendationId={}, priority={}",
                        teamId,
                        recommendation.getId(),
                        recommendationPriority
                );
            }
        } else {
            if (optionalRecommendation.isPresent()) {
                Recommendation recommendation = optionalRecommendation.get();

                recommendation.setStatus(RecommendationStatus.RESOLVED);
                recommendation.setResolvedAt(Instant.now());

                log.info(
                        "Recommendation resolved: teamId={}, recommendationId={}",
                        teamId,
                        recommendation.getId()
                );
            }
        }
    }
}
