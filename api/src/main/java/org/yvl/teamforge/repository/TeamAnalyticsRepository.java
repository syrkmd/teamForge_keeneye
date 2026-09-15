package org.yvl.teamforge.repository;

import org.springframework.data.repository.Repository;
import org.yvl.teamforge.entity.TeamAnalyticsMaterializedView;

import java.util.Optional;

public interface TeamAnalyticsRepository extends Repository<TeamAnalyticsMaterializedView, Long> {

    Optional<TeamAnalyticsMaterializedView> findByTeamId(Long teamId);
}
