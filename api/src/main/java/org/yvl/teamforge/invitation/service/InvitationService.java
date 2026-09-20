package org.yvl.teamforge.invitation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.*;
import org.yvl.teamforge.exception.UserNotFoundException;
import org.yvl.teamforge.invitation.exception.InvitationAccessDeniedException;
import org.yvl.teamforge.invitation.exception.InvitationAlreadyExistsException;
import org.yvl.teamforge.invitation.exception.InvitationAlreadyRespondedException;
import org.yvl.teamforge.invitation.exception.InvitationNotFoundException;
import org.yvl.teamforge.notification.event.NotificationRequestedEvent;
import org.yvl.teamforge.repository.*;
import org.yvl.teamforge.team.exception.TeamNotFoundException;
import org.yvl.teamforge.invitation.dto.request.InvitationCreateRequest;
import org.yvl.teamforge.invitation.dto.response.InvitationView;
import org.yvl.teamforge.invitation.mapper.InvitationMapper;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.team.service.TeamService;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final NotificationRepository notificationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

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

        createAndPublishNotification(
                user,
                NotificationType.INVITATION_RECEIVED,
                "New project invitation",
                "You have been invited to the role " + projectRole.getRoleName()
                        + " in the project " + projectRole.getProject().getName(),
                invitation,
                null
        );

        return mapper.toInvitationView(invitation);
    }

    public InvitationView declineInvitation(
            UserPrincipal userPrincipal,
            Long invitationId
    ) {
        Invitation invitation = getPendingInvitation(userPrincipal, invitationId);

        invitation.setStatus(InvitationStatus.DECLINED);
        invitation.setRespondedAt(Instant.now());

        createAndPublishNotification(
                invitation.getProject().getOwner(),
                NotificationType.INVITATION_REJECTED,
                "Invitation declined",
                "The invitation for the role " + invitation.getProjectRole().getRoleName()
                        + " in the project " + invitation.getProject().getName()
                        + " was declined by " + userPrincipal.getUser().getLastName() + " " + userPrincipal.getUser().getFirstName(),
                invitation,
                null
        );

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

        createAndPublishNotification(
                project.getOwner(),
                NotificationType.INVITATION_ACCEPTED,
                "Invitation accepted",
                "The invitation for the role " + projectRole.getRoleName()
                        + " in the project " + project.getName()
                        + " was accepted by " + userPrincipal.getUser().getLastName() + " " + userPrincipal.getUser().getFirstName(),
                invitation,
                null
        );

        boolean wasCompletedBefore = team.getStatus() == TeamStatus.COMPLETED;

        if (!projectRoleRepository.existsByProjectIdAndStatus(project.getId(), ProjectRoleStatus.OPEN)) {
            team.setStatus(TeamStatus.COMPLETED);
        }

        if (!wasCompletedBefore && team.getStatus() == TeamStatus.COMPLETED) {
            Map<Long, User> recipients = new LinkedHashMap<>();

            recipients.put(project.getOwner().getId(), project.getOwner());

            List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndStatus(team.getId(), TeamMemberStatus.ACTIVE);

            teamMembers.forEach(member ->
                recipients.putIfAbsent(member.getUser().getId(), member.getUser())
            );

            recipients.values().forEach(user ->
                createAndPublishNotification(
                        user,
                        NotificationType.TEAM_FORMATION_COMPLETED,
                        "Team formed",
                        "The team for project " + project.getName() + " is fully staffed.",
                        null,
                        team
                )
            );
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

    private void createAndPublishNotification(
            User user,
            NotificationType type,
            String title,
            String message,
            Invitation invitation,
            Team team
    ) {
        Notification notification = notificationRepository.save(Notification.builder()
                .type(type)
                .user(user)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(Instant.now())
                .invitation(invitation)
                .team(team)
                .build());

        applicationEventPublisher.publishEvent(new NotificationRequestedEvent(
                notification.getId(),
                user.getId(),
                notification.getType()
        ));
    }
}
