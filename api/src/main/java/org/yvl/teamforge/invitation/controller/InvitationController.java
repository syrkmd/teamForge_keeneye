package org.yvl.teamforge.invitation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.invitation.dto.request.InvitationCreateRequest;
import org.yvl.teamforge.invitation.dto.response.InvitationView;
import org.yvl.teamforge.invitation.service.InvitationService;
import org.yvl.teamforge.security.user.UserPrincipal;

@Validated
@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping("/projects/{projectId}/roles/{roleId}/invitations")
    public Page<InvitationView> getInvitations(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId
    ) {
        return invitationService.getInvitations(userPrincipal, roleId, projectId, PageRequest.of(page, size));
    }

    @PostMapping("/projects/{projectId}/roles/{roleId}/invitations")
    public InvitationView createInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId,
            @Valid @RequestBody InvitationCreateRequest request
    ) {
        return invitationService.createInvitation(userPrincipal, projectId, roleId, request);
    }

    @GetMapping("/invitations/me")
    public Page<InvitationView> getMyInvitations(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return invitationService.getMyInvitations(userPrincipal, PageRequest.of(page, size));
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public InvitationView acceptInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long invitationId
    ) {
        return invitationService.acceptInvitation(userPrincipal, invitationId);
    }

    @PostMapping("/invitations/{invitationId}/decline")
    public InvitationView declineInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long invitationId
    ) {
        return invitationService.declineInvitation(userPrincipal, invitationId);
    }
}
