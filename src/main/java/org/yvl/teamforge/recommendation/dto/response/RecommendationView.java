package org.yvl.teamforge.recommendation.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.RecommendationPriority;
import org.yvl.teamforge.entity.enums.RecommendationStatus;

import java.time.Instant;

@Data
public class RecommendationView {

    private Long id;
    private String title;
    private String description;
    private RecommendationPriority priority;
    private RecommendationStatus status;
    private Instant createdAt;
    private Instant resolvedAt;
}
