package org.yvl.teamforge.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yvl.teamforge.analytics.dto.response.SkillGapView;
import org.yvl.teamforge.analytics.dto.response.TeamAnalyticsView;
import org.yvl.teamforge.analytics.service.AnalyticsService;
import org.yvl.teamforge.analytics.service.SkillGapService;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;
    private final SkillGapService skillGapService;

    @GetMapping("/projects/{projectId}/analytics")
    public TeamAnalyticsView getProjectAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId
    ) {
        return service.getTeamAnalytics(userPrincipal, projectId);
    }

    @GetMapping("/projects/{projectId}/analytics/skill-gap")
    public List<SkillGapView> getSkillGaps(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId
    ) {
        return skillGapService.getSkillGaps(userPrincipal, projectId);
    }
}
