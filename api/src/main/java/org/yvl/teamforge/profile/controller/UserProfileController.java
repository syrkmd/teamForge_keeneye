package org.yvl.teamforge.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.profile.dto.request.ProfileUpdateRequest;
import org.yvl.teamforge.profile.dto.response.UserProfileView;
import org.yvl.teamforge.profile.service.UserProfileService;
import org.yvl.teamforge.security.user.UserPrincipal;

@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    @GetMapping
    public UserProfileView getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return service.getProfile(userPrincipal);
    }

    @PatchMapping
    public UserProfileView patchProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return service.profileUpdate(userPrincipal, request);
    }
}
