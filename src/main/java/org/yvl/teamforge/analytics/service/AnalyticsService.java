package org.yvl.teamforge.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.analytics.dto.response.TeamAnalyticsView;
import org.yvl.teamforge.analytics.mapper.AnalyticsMapper;
import org.yvl.teamforge.entity.Team;

import org.yvl.teamforge.entity.TeamAnalyticsMaterializedView;
import org.yvl.teamforge.analytics.exception.TeamAnalyticsNotFoundException;
import org.yvl.teamforge.team.exception.TeamNotFoundException;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.repository.TeamAnalyticsRepository;
import org.yvl.teamforge.repository.TeamRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TeamAnalyticsRepository teamAnalyticsRepository;
    private final TeamRepository teamRepository;
    private final ProjectAccessService projectAccessService;
    private final AnalyticsMapper mapper;

    public TeamAnalyticsView getTeamAnalytics(
            UserPrincipal userPrincipal,
            Long projectId
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        Team team = teamRepository.findByProjectId(projectId).orElseThrow(() ->
                new TeamNotFoundException(projectId));

        TeamAnalyticsMaterializedView materializedView = teamAnalyticsRepository.findByTeamId(team.getId())
                .orElseThrow(TeamAnalyticsNotFoundException::new);

        return mapper.toTeamAnalyticsView(materializedView);
    }
}
