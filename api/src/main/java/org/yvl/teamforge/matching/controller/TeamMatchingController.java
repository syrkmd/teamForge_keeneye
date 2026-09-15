package org.yvl.teamforge.matching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yvl.teamforge.matching.dto.response.ProjectRoleCandidatesView;
import org.yvl.teamforge.matching.service.TeamMatchingService;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamMatchingController {

    private final TeamMatchingService service;

    @GetMapping("/projects/{projectId}/roles/{roleId}/candidates")
    public ProjectRoleCandidatesView getProjectCandidates(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId
    ) {
        return service.getCandidates(userPrincipal, projectId, roleId);
    }

    @GetMapping("/projects/{projectId}/candidates")
    public List<ProjectRoleCandidatesView> getProjectCandidatesForProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId
    ) {
        return service.getCandidatesForProject(userPrincipal, projectId);
    }
}
