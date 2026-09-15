package org.yvl.teamforge.analytics.scheduler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TeamAnalyticsRefreshScheduler {

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(fixedDelayString = "${analytics.refresh-delay}")
    @Transactional
    public void refreshTeamAnalytics() {
        entityManager
                .createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY team_analytics")
                .executeUpdate();
    }
}
