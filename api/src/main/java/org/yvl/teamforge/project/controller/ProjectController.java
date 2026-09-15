package org.yvl.teamforge.project.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.project.dto.request.*;
import org.yvl.teamforge.project.dto.response.ProjectRoleSkillView;
import org.yvl.teamforge.project.dto.response.ProjectRoleView;
import org.yvl.teamforge.project.dto.response.ProjectView;
import org.yvl.teamforge.project.service.ProjectRoleService;
import org.yvl.teamforge.project.service.ProjectRoleSkillService;
import org.yvl.teamforge.project.service.ProjectService;
import org.yvl.teamforge.security.user.UserPrincipal;

@Validated
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;
    private final ProjectRoleService roleService;
    private final ProjectRoleSkillService roleSkillService;

    @GetMapping
    public Page<ProjectView> getProjects(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size
    ) {
        return service.getProjects(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ProjectView getProjectById(
            @PathVariable Long id
    ) {
        return service.getProjectById(id);
    }

    @PostMapping
    public ProjectView createProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        return service.createProject(userPrincipal, request);
    }

    @PatchMapping("/{id}")
    public ProjectView patchProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        return service.patchProject(userPrincipal, id, request);
    }

    @PostMapping("/{id}/archive")
    public void archiveProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id
    ) {
        service.archiveProject(userPrincipal, id);
    }

    @GetMapping("/{projectId}/roles")
    public Page<ProjectRoleView> getProjectRoles(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size,
            @PathVariable Long projectId
    ) {
        return roleService.getProjectRoles(projectId, PageRequest.of(page, size));
    }

    @PostMapping("/{projectId}/roles")
    public ProjectRoleView createProjectRole(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRoleCreateRequest request
    ) {
        return roleService.createProjectRole(userPrincipal, projectId, request);
    }

    @PatchMapping("/{projectId}/roles/{roleId}")
    public ProjectRoleView patchProjectRole(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId,
            @Valid @RequestBody ProjectRoleUpdateRequest request
    ) {
        return roleService.patchProjectRole(userPrincipal, projectId, roleId, request);
    }

    @DeleteMapping("/{projectId}/roles/{roleId}")
    public void deleteProjectRole(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId
    ) {
        roleService.deleteProjectRole(userPrincipal, projectId, roleId);
    }

    @GetMapping("/{projectId}/roles/{roleId}/skills")
    public Page<ProjectRoleSkillView> getProjectRoleSkills(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size,
            @PathVariable Long projectId,
            @PathVariable Long roleId
    ) {
        return roleSkillService.getRoleSkills(projectId, roleId, PageRequest.of(page, size));
    }

    @PostMapping("/{projectId}/roles/{roleId}/skills")
    public ProjectRoleSkillView createProjectRoleSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId,
            @Valid @RequestBody ProjectRoleSkillCreateRequest request
    ) {
        return roleSkillService.addRoleSkill(userPrincipal, projectId, roleId, request);
    }

    @PatchMapping("/{projectId}/roles/{roleId}/skills/{skillId}")
    public ProjectRoleSkillView patchProjectRoleSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId,
            @PathVariable Long skillId,
            @Valid @RequestBody ProjectRoleSkillUpdateRequest request
    ) {
        return roleSkillService.patchMinLevel(userPrincipal, projectId, roleId, skillId, request);
    }


    @DeleteMapping("/{projectId}/roles/{roleId}/skills/{skillId}")
    public void deleteProjectRoleSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long projectId,
            @PathVariable Long roleId,
            @PathVariable Long skillId
    ) {
        roleSkillService.removeRoleSkill(userPrincipal, projectId, roleId, skillId);
    }
}
