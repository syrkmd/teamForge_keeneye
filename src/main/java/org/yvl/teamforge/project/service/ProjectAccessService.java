package org.yvl.teamforge.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yvl.teamforge.entity.Project;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.ProjectRoleSkill;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.entity.enums.ProjectStatus;
import org.yvl.teamforge.exception.*;
import org.yvl.teamforge.repository.ProjectRepository;
import org.yvl.teamforge.repository.ProjectRoleRepository;
import org.yvl.teamforge.repository.ProjectRoleSkillRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

@Service
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRoleSkillRepository projectRoleSkillRepository;

    public Project getProjectForModification(UserPrincipal userPrincipal, Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ProjectNotFoundException(projectId));

        if (!project.getOwner().getId().equals(userPrincipal.getUser().getId())) {
            throw new ProjectAccessDeniedException();
        }

        if (project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new ProjectArchivedException();
        }

        return project;
    }

    public ProjectRole getProjectRoleForModification(
            UserPrincipal userPrincipal,
            Long projectId,
            Long projectRoleId
    ) {
        Project project = getProjectForModification(userPrincipal, projectId);

        ProjectRole projectRole = projectRoleRepository.findById(projectRoleId).orElseThrow(() ->
                new ProjectRoleNotFoundException(projectRoleId));

        if (!projectRole.getProject().getId().equals(project.getId())) {
            throw new ProjectRoleNotBelongToProjectException(projectRoleId, projectId);
        }

        return projectRole;
    }

    public ProjectRoleSkill getProjectRoleSkillForModification(
            UserPrincipal userPrincipal,
            Long projectId,
            Long projectRoleId,
            Long skillId
    ) {
        Project project = getProjectForModification(userPrincipal, projectId);

        ProjectRole projectRole = projectRoleRepository.findById(projectRoleId).orElseThrow(() ->
                new ProjectRoleNotFoundException(projectRoleId));

        if (!projectRole.getProject().getId().equals(project.getId())) {
            throw new ProjectRoleNotBelongToProjectException(projectRoleId, projectId);
        }

        return projectRoleSkillRepository.findByProjectRoleIdAndSkillId(projectRoleId, skillId).orElseThrow(() ->
                new ProjectRoleSkillNotFoundException(projectRoleId, skillId));
    }

    public ProjectRole getProjectRoleForMatching(
            UserPrincipal userPrincipal,
            Long projectId,
            Long projectRoleId
    ) {
        Project project = getProjectForModification(userPrincipal, projectId);

        ProjectRole projectRole = projectRoleRepository.findById(projectRoleId).orElseThrow(() ->
                new ProjectRoleNotFoundException(projectRoleId));

        if (!projectRole.getProject().getId().equals(project.getId())) {
            throw new ProjectRoleNotBelongToProjectException(projectRoleId, projectId);
        }

        if (!projectRole.getStatus().equals(ProjectRoleStatus.OPEN)) {
            throw new ProjectRoleNotOpenException(projectRoleId);
        }

        return projectRole;
    }
}
