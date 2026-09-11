package org.yvl.teamforge.recommendation.mapper;

import org.mapstruct.Mapper;
import org.yvl.teamforge.entity.Recommendation;
import org.yvl.teamforge.entity.enums.RecommendationPriority;
import org.yvl.teamforge.entity.enums.RiskLevel;
import org.yvl.teamforge.recommendation.dto.response.RecommendationView;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    RecommendationView toRecommendationView(Recommendation recommendation);

    RecommendationPriority toRecommendationPriority(RiskLevel riskLevel);
}
