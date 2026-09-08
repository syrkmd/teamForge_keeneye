package org.yvl.teamforge.invitation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.InvitationStatus;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.entity.enums.TeamStatus;
import org.yvl.teamforge.exception.*;
import org.yvl.teamforge.invitation.dto.request.InvitationCreateRequest;
import org.yvl.teamforge.invitation.dto.response.InvitationView;
import org.yvl.teamforge.invitation.mapper.InvitationMapper;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.repository.InvitationRepository;
import org.yvl.teamforge.repository.ProjectRoleRepository;
import org.yvl.teamforge.repository.TeamRepository;
import org.yvl.teamforge.repository.UserRepository;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.team.service.TeamService;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final ProjectAccessService projectAccessService;
    private final TeamService teamService;
    private final InvitationMapper mapper;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectRoleRepository projectRoleRepository;

    public Page<InvitationView> getInvitations(
            UserPrincipal userPrincipal,
            Long roleId,
            Long projectId,
            Pageable pageable
    ) {
        projectAccessService.getProjectRoleForMatching(userPrincipal, projectId, roleId);
        return invitationRepository.findByProjectRoleId(roleId, pageable).map(mapper::toInvitationView);
    }

    public InvitationView createInvitation(
            UserPrincipal userPrincipal,
            Long projectId,
            Long roleId,
            InvitationCreateRequest request
    ) {
        ProjectRole projectRole = projectAccessService.getProjectRoleForMatching(userPrincipal, projectId, roleId);

        User user = userRepository.findById(request.getUserId()).orElseThrow(() ->
                new UserNotFoundException(request.getUserId()));

        if (invitationRepository.existsByProjectRoleIdAndUserIdAndStatus(roleId, request.getUserId(), InvitationStatus.PENDING)) {
            throw new InvitationAlreadyExistsException();
        }

        teamService.getOrCreateTeam(projectRole.getProject());

        Invitation invitation = invitationRepository.save(Invitation.builder()
                .status(InvitationStatus.PENDING)
                .createdAt(Instant.now())
                .project(projectRole.getProject())
                .projectRole(projectRole)
                .user(user)
                .build());

        return mapper.toInvitationView(invitation);
    }

    public InvitationView declineInvitation(
            UserPrincipal userPrincipal,
            Long invitationId
    ) {
        Invitation invitation = getPendingInvitation(userPrincipal, invitationId);

        invitation.setStatus(InvitationStatus.DECLINED);
        invitation.setRespondedAt(Instant.now());

        return mapper.toInvitationView(invitation);
    }

    public InvitationView acceptInvitation(
            UserPrincipal userPrincipal,
            Long invitationId
    ) {
        Invitation invitation = getPendingInvitation(userPrincipal, invitationId);

        ProjectRole projectRole = invitation.getProjectRole();

        Project project = invitation.getProject();

        projectAccessService.getProjectRoleForInvitation(project.getId(), projectRole.getId());

        Team team = teamRepository.findByProjectId(project.getId()).orElseThrow(() ->
                new TeamNotFoundException(project.getId()));

        teamService.addMember(team, userPrincipal.getUser(), projectRole);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(Instant.now());

        if (!projectRoleRepository.existsByProjectIdAndStatus(project.getId(), ProjectRoleStatus.OPEN)) {
            team.setStatus(TeamStatus.COMPLETED);
        }

        return mapper.toInvitationView(invitation);
    }

    public Page<InvitationView> getMyInvitations(
            UserPrincipal userPrincipal,
            Pageable pageable
    ) {
        return invitationRepository.findByUserId(userPrincipal.getUser().getId(), pageable).map(mapper::toInvitationView);
    }

    private Invitation getPendingInvitation(
            UserPrincipal userPrincipal,
            Long invitationId
    ) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow(() ->
                new InvitationNotFoundException(invitationId));

        if (!userPrincipal.getUser().getId().equals(invitation.getUser().getId())) {
            throw new InvitationAccessDeniedException();
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyRespondedException();
        }

        return invitation;
    }
}
