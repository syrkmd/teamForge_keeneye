package org.yvl.teamforge.admin.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.admin.dto.request.RoleChangeRequest;
import org.yvl.teamforge.admin.dto.request.StatusChangeRequest;
import org.yvl.teamforge.admin.dto.response.UserAdminView;
import org.yvl.teamforge.admin.service.AdminUserService;

@Validated
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping("/users")
    public Page<UserAdminView> getUsers(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size
    ) {
        return service.getUsers(PageRequest.of(page, size));
    }

    @PatchMapping("/users/{userId}/role")
    public UserAdminView patchRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleChangeRequest request
    ) {
        return service.changeRole(userId, request.getRole());
    }

    @PatchMapping("/users/{userId}/status")
    public UserAdminView patchStatus(
            @PathVariable Long userId,
            @Valid @RequestBody StatusChangeRequest request
    ) {
        return service.changeActive(userId, request.getActive());
    }
}
