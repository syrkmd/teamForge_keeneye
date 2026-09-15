package org.yvl.teamforge.team.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.team.dto.request.TeamMemberActionRequest;
import org.yvl.teamforge.team.dto.response.TeamMemberView;
import org.yvl.teamforge.team.dto.response.TeamView;
import org.yvl.teamforge.team.service.TeamService;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/projects/{projectId}/team")
    public TeamView getTeam(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId
    ) {
        return teamService.getTeamByProjectId(userPrincipal, projectId);
    }

    @GetMapping("/projects/{projectId}/team/members")
    public Page<TeamMemberView> getTeamMembers(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId
    ) {
        return teamService.getTeamMembers(userPrincipal, projectId, PageRequest.of(page, size));
    }

    @PostMapping("/projects/{projectId}/team/members/{memberId}/leave")
    public TeamMemberView leaveTeamMember(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody TeamMemberActionRequest request

            ) {
        return teamService.leave(userPrincipal, projectId, memberId, request);
    }

    @PostMapping("/projects/{projectId}/team/members/{memberId}/remove")
    public TeamMemberView removeTeamMember(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody TeamMemberActionRequest request

    ) {
        return teamService.removeMember(userPrincipal, projectId, memberId, request);
    }
}
