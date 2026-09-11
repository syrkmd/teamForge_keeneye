package org.yvl.teamforge.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yvl.teamforge.recommendation.dto.response.RecommendationView;
import org.yvl.teamforge.recommendation.service.RecommendationService;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/projects/{projectId}/recommendations")
    public List<RecommendationView> getRecommendations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId
    ) {
        return recommendationService.getRecommendations(userPrincipal, projectId);
    }
}
