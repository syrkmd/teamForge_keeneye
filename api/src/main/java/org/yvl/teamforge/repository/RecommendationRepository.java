package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.Recommendation;
import org.yvl.teamforge.entity.enums.RecommendationStatus;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    Optional<Recommendation> findByTeamIdAndStatus(Long teamId, RecommendationStatus status);
}
