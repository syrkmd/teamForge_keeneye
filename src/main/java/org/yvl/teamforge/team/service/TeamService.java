package org.yvl.teamforge.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.TeamMemberStatus;
import org.yvl.teamforge.entity.enums.TeamStatus;
import org.yvl.teamforge.exception.*;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.project.service.ProjectRoleStatusService;
import org.yvl.teamforge.repository.TeamMemberRepository;
import org.yvl.teamforge.repository.TeamRepository;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.team.dto.request.TeamMemberActionRequest;
import org.yvl.teamforge.team.dto.response.TeamMemberView;
import org.yvl.teamforge.team.dto.response.TeamView;
import org.yvl.teamforge.team.mapper.TeamMapper;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectAccessService projectAccessService;
    private final ProjectRoleStatusService projectRoleStatusService;
    private final TeamMapper mapper;

    public TeamView getTeamByProjectId(
            UserPrincipal userPrincipal,
            Long projectId
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        Team team = teamRepository.findByProjectId(projectId).orElseThrow(() ->
                new TeamNotFoundException(projectId));

        return mapper.toTeamView(team);
    }

    public Team getOrCreateTeam(
            Project project
    ) {
        return teamRepository.findByProjectId(project.getId())
                .orElseGet(() -> teamRepository.save(
                        Team.builder()
                                .status(TeamStatus.FORMING)
                                .createdAt(Instant.now())
                                .project(project)
                                .build()
                ));
    }

    public TeamMember addMember(
            Team team,
            User user,
            ProjectRole projectRole
    ) {

        if (teamMemberRepository.existsByTeamIdAndUserIdAndProjectRoleIdAndStatus(
                team.getId(),
                user.getId(),
                projectRole.getId(),
                TeamMemberStatus.ACTIVE
        )) {
            throw new UserAlreadyTeamMemberException();
        }

        TeamMember teamMember = teamMemberRepository.save(
                TeamMember.builder()
                        .team(team)
                        .user(user)
                        .projectRole(projectRole)
                        .status(TeamMemberStatus.ACTIVE)
                        .joinedAt(Instant.now())
                        .build()
        );

        projectRoleStatusService.updateStatus(projectRole);

        return teamMember;
    }

    public Page<TeamMemberView> getTeamMembers(
            UserPrincipal userPrincipal,
            Long projectId,
            Pageable pageable
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        Team team = teamRepository.findByProjectId(projectId).orElseThrow(() ->
                new TeamNotFoundException(projectId));

        return teamMemberRepository.findByTeamId(team.getId(), pageable).map(mapper::toTeamMemberView);
    }

    public TeamMemberView leave(
            UserPrincipal userPrincipal,
            Long projectId,
            Long memberId,
            TeamMemberActionRequest request
    ) {
        TeamMember teamMember = teamMemberRepository.findById(memberId).orElseThrow(() ->
                new TeamMemberNotFoundException(memberId));

        if (!teamMember.getUser().getId().equals(userPrincipal.getUser().getId())
                || !teamMember.getTeam().getProject().getId().equals(projectId)) {
            throw new TeamMemberAccessDeniedException();
        }

        if (teamMember.getStatus() == TeamMemberStatus.INACTIVE) {
            throw new TeamMemberAlreadyInactiveException();
        }

        teamMember.setStatus(TeamMemberStatus.INACTIVE);
        teamMember.setLeftAt(Instant.now());
        teamMember.setReason(request.getReason());

        ProjectRole projectRole = teamMember.getProjectRole();

        projectRoleStatusService.updateStatus(projectRole);

        return mapper.toTeamMemberView(teamMember);
    }

    public TeamMemberView removeMember(
            UserPrincipal userPrincipal,
            Long projectId,
            Long memberId,
            TeamMemberActionRequest request
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        TeamMember teamMember = teamMemberRepository.findById(memberId).orElseThrow(() ->
                new TeamMemberNotFoundException(memberId));

        if (teamMember.getStatus() == TeamMemberStatus.INACTIVE) {
            throw new TeamMemberAlreadyInactiveException();
        }

        if (!teamMember.getTeam().getProject().getId().equals(projectId)) {
            throw new TeamMemberAccessDeniedException();
        }

        teamMember.setStatus(TeamMemberStatus.INACTIVE);
        teamMember.setLeftAt(Instant.now());
        teamMember.setReason(request.getReason());

        ProjectRole projectRole = teamMember.getProjectRole();

        projectRoleStatusService.updateStatus(projectRole);

        return mapper.toTeamMemberView(teamMember);
    }
}
