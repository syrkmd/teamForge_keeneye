package org.yvl.teamforge.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.Project;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.exception.ProjectNotFoundException;
import org.yvl.teamforge.exception.ProjectRoleNotBelongToProjectException;
import org.yvl.teamforge.exception.ProjectRoleNotFoundException;
import org.yvl.teamforge.project.dto.request.ProjectRoleCreateRequest;
import org.yvl.teamforge.project.dto.request.ProjectRoleUpdateRequest;
import org.yvl.teamforge.project.dto.response.ProjectRoleView;
import org.yvl.teamforge.project.mapper.ProjectMapper;
import org.yvl.teamforge.repository.*;
import org.yvl.teamforge.security.user.UserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectRoleService {

    private final ProjectRoleRepository repository;
    private final ProjectRepository projectRepository;
    private final InvitationRepository invitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRoleSkillRepository projectRoleSkillRepository;
    private final ProjectAccessService projectAccessService;
    private final ProjectRoleStatusService projectRoleStatusService;
    private final ProjectMapper mapper;

    public Page<ProjectRoleView> getProjectRoles(Long projectId, Pageable pageable) {
        projectRepository.findById(projectId).orElseThrow(() ->
                new ProjectNotFoundException(projectId));

        Page<ProjectRole> projectRoles = repository.findByProjectId(projectId, pageable);

        return projectRoles.map(mapper::toProjectRoleView);
    }

    public ProjectRoleView createProjectRole(UserPrincipal userPrincipal, Long projectId, ProjectRoleCreateRequest request) {
        Project project = projectAccessService.getProjectForModification(userPrincipal, projectId);

        ProjectRole projectRole = repository.save(
                ProjectRole.builder()
                        .roleName(request.getRoleName())
                        .status(ProjectRoleStatus.OPEN)
                        .requiredCount(request.getRequiredCount())
                        .description(request.getDescription())
                        .project(project)
                        .build());


        return mapper.toProjectRoleView(projectRole);
    }

    public ProjectRoleView patchProjectRole(UserPrincipal userPrincipal, Long projectId, Long roleId, ProjectRoleUpdateRequest request) {
        Project project = projectAccessService.getProjectForModification(userPrincipal, projectId);

        ProjectRole projectRole = repository.findById(roleId).orElseThrow(() ->
                new ProjectRoleNotFoundException(roleId));

        if (!projectRole.getProject().getId().equals(project.getId())) {
            throw new ProjectRoleNotBelongToProjectException(roleId, projectId);
        }

        if (request.getRoleName() != null) {
            projectRole.setRoleName(request.getRoleName());
        }

        if (request.getDescription() != null) {
            projectRole.setDescription(request.getDescription());
        }

        if (request.getRequiredCount() != null) {
            projectRole.setRequiredCount(request.getRequiredCount());
            projectRoleStatusService.updateStatus(projectRole);
        }

        return mapper.toProjectRoleView(projectRole);
    }

    public void deleteProjectRole(UserPrincipal userPrincipal, Long projectId, Long roleId) {
        Project project = projectAccessService.getProjectForModification(userPrincipal, projectId);

        ProjectRole projectRole = repository.findById(roleId).orElseThrow(() ->
                new ProjectRoleNotFoundException(roleId));

        if (!projectRole.getProject().getId().equals(project.getId())) {
            throw new ProjectRoleNotBelongToProjectException(roleId, projectId);
        }

        projectRoleSkillRepository.deleteAllByProjectRoleId(roleId);

        invitationRepository.deleteAllByProjectRoleId(roleId);
        teamMemberRepository.deleteAllByProjectRoleId(roleId);

        repository.delete(projectRole);
    }
}
