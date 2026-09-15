package org.yvl.teamforge.recommendation.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yvl.teamforge.entity.Team;
import org.yvl.teamforge.recommendation.service.RecommendationGenerationService;
import org.yvl.teamforge.repository.TeamRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationFallbackScheduler {

    private final TeamRepository teamRepository;
    private final RecommendationGenerationService recommendationGenerationService;

    @Scheduled(fixedDelayString = "${recommendation.fallback-delay}")
    public void regenerateRecommendations() {
        log.info("Recommendation fallback regeneration started");

        List<Team> teams = teamRepository.findAll();

        teams.forEach(team -> {
            try {
                recommendationGenerationService.generateOrUpdate(team.getId());
            } catch (Exception e) {
                log.error(
                        "Recommendation fallback failed: teamId={}",
                        team.getId(),
                        e
                );
            }
        });


        log.info(
                "Recommendation fallback regeneration completed: teamsCount={}",
                teams.size()
        );
    }
}
