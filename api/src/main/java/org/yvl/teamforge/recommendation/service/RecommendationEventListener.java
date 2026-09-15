package org.yvl.teamforge.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.yvl.teamforge.recommendation.event.TeamCompositionChangedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationEventListener {

    private final RecommendationGenerationService recommendationGenerationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamCompositionChanged(TeamCompositionChangedEvent event) {
        log.info("Recommendation event received: teamId={}", event.getTeamId());
        recommendationGenerationService.generateOrUpdate(event.getTeamId());
    }
}
