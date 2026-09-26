package org.yvl.teamforge.project.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.entity.enums.ProjectStatus;
import org.yvl.teamforge.project.exception.InvalidProjectStatusTransitionException;
import org.yvl.teamforge.project.exception.ProjectAccessDeniedException;
import org.yvl.teamforge.project.exception.ProjectNotFoundException;
import org.yvl.teamforge.project.exception.ProjectTemplateNotFoundException;
import org.yvl.teamforge.project.dto.request.ProjectCreateRequest;
import org.yvl.teamforge.project.dto.request.ProjectUpdateRequest;
import org.yvl.teamforge.project.dto.response.ProjectView;
import org.yvl.teamforge.project.mapper.ProjectMapper;
import org.yvl.teamforge.repository.*;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectTemplateRepository templateRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRoleSkillRepository projectRoleSkillRepository;
    private final TemplateRoleSkillRepository templateRoleSkillRepository;
    private final ProjectAccessService projectAccessService;
    private final ProjectMapper mapper;
    private final EntityManager entityManager;

    public Page<ProjectView> getProjects(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toProjectView);
    }

    public ProjectView getProjectById(Long id) {
        Project project = repository.findById(id).orElseThrow(() ->
                new ProjectNotFoundException(id));

        return mapper.toProjectView(project);
    }

    public ProjectView createProject(UserPrincipal userPrincipal, ProjectCreateRequest request) {
        var template = templateRepository.findByIdAndIsActiveTrue(request.getTemplateId()).orElseThrow(() ->
                new ProjectTemplateNotFoundException(request.getTemplateId()));

        Instant now = Instant.now();

        User owner = entityManager.getReference(
                User.class,
                userPrincipal.getUser().getId()
        );

        Project project = repository.save(Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(ProjectStatus.DRAFT)
                .deadline(request.getDeadline())
                .createdAt(now)
                .updatedAt(now)
                .template(template)
                .owner(owner)
                .build());

        List<TemplateRole> templateRoles = template.getTemplateRoles();

        List<ProjectRole> projectRoles = templateRoles
                .stream()
                .map(templateRole -> ProjectRole.builder()
                            .project(project)
                            .description(templateRole.getDescription())
                            .roleName(templateRole.getRoleName())
                            .requiredCount(templateRole.getRequiredCount())
                            .status(ProjectRoleStatus.OPEN)
                            .build()
                ).toList();

        Map<Long, ProjectRole> roleMap = new HashMap<>();

        for (int i = 0; i < templateRoles.size(); i++) {
            roleMap.put(templateRoles.get(i).getId(), projectRoles.get(i));
        }

        projectRoleRepository.saveAll(projectRoles);

        List<TemplateRoleSkill> templateRoleSkills = templateRoleSkillRepository.findByTemplateRoleIdIn(templateRoles
                .stream()
                .map(TemplateRole::getId)
                .toList());


        List<ProjectRoleSkill> projectRoleSkills = templateRoleSkills
                .stream()
                .map(templateRoleSkill -> ProjectRoleSkill.builder()
                        .minLevel(templateRoleSkill.getMinLevel())
                        .projectRole(
                                roleMap.get(templateRoleSkill.getTemplateRole().getId())
                        )
                        .skill(templateRoleSkill.getSkill())
                        .build()
                ).toList();

        projectRoleSkillRepository.saveAll(projectRoleSkills);

        return mapper.toProjectView(project);
    }

    public ProjectView patchProject(UserPrincipal userPrincipal, Long id, ProjectUpdateRequest request) {

        Project project = projectAccessService.getProjectForModification(userPrincipal, id);

        if (request.getName() != null) {
            project.setName(request.getName());
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (request.getDeadline() != null) {
            project.setDeadline(request.getDeadline());
        }

        project.setUpdatedAt(Instant.now());

        return mapper.toProjectView(project);
    }

    public void archiveProject(UserPrincipal userPrincipal, Long id) {
        Project project = repository.findById(id).orElseThrow(() ->
                new ProjectNotFoundException(id));

        if (!project.getOwner().getId().equals(userPrincipal.getUser().getId())) {
            throw new ProjectAccessDeniedException();
        }

        if (project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new InvalidProjectStatusTransitionException();
        }

        project.setStatus(ProjectStatus.ARCHIVED);
        project.setUpdatedAt(Instant.now());
    }
}
