package org.yvl.teamforge.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.Project;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.ProjectRoleSkill;
import org.yvl.teamforge.entity.Skill;
import org.yvl.teamforge.project.exception.ProjectNotFoundException;
import org.yvl.teamforge.project.exception.ProjectRoleNotBelongToProjectException;
import org.yvl.teamforge.project.exception.ProjectRoleNotFoundException;
import org.yvl.teamforge.project.exception.ProjectRoleSkillAlreadyExistsException;
import org.yvl.teamforge.skill.exception.SkillNotFoundException;
import org.yvl.teamforge.project.dto.request.ProjectRoleSkillCreateRequest;
import org.yvl.teamforge.project.dto.request.ProjectRoleSkillUpdateRequest;
import org.yvl.teamforge.project.dto.response.ProjectRoleSkillView;
import org.yvl.teamforge.project.mapper.ProjectMapper;
import org.yvl.teamforge.repository.ProjectRepository;
import org.yvl.teamforge.repository.ProjectRoleRepository;
import org.yvl.teamforge.repository.ProjectRoleSkillRepository;
import org.yvl.teamforge.repository.SkillRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectRoleSkillService {

    private final ProjectRoleSkillRepository repository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ProjectAccessService projectAccessService;
    private final ProjectMapper mapper;

    public Page<ProjectRoleSkillView> getRoleSkills(Long projectId, Long projectRoleId, Pageable pageable) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ProjectNotFoundException(projectId));

        ProjectRole projectRole = projectRoleRepository.findById(projectRoleId).orElseThrow(() ->
                new ProjectRoleNotFoundException(projectRoleId));

        if (!projectRole.getProject().getId().equals(project.getId())) {
            throw new ProjectRoleNotBelongToProjectException(projectRoleId, projectId);
        }

        Page<ProjectRoleSkill> projectRoleSkills = repository.findByProjectRoleId(projectRoleId, pageable);

        return projectRoleSkills.map(mapper::toProjectRoleSkillView);
    }

    public ProjectRoleSkillView addRoleSkill(UserPrincipal userPrincipal, Long projectId, Long projectRoleId, ProjectRoleSkillCreateRequest request) {
        ProjectRole projectRole = projectAccessService.getProjectRoleForModification(userPrincipal, projectId, projectRoleId);

        Skill skill = skillRepository.findById(request.getSkillId()).orElseThrow(() ->
                new SkillNotFoundException(request.getSkillId()));

        if (repository.existsByProjectRoleIdAndSkillId(projectRoleId, request.getSkillId())) {
            throw new ProjectRoleSkillAlreadyExistsException(projectRoleId, request.getSkillId());
        }

        ProjectRoleSkill projectRoleSkill = repository.save(
                ProjectRoleSkill.builder()
                        .projectRole(projectRole)
                        .minLevel(request.getMinLevel())
                        .skill(skill)
                        .build()
        );

        return mapper.toProjectRoleSkillView(projectRoleSkill);
    }

    public ProjectRoleSkillView patchMinLevel(UserPrincipal userPrincipal, Long projectId, Long projectRoleId, Long skillId, ProjectRoleSkillUpdateRequest request) {
        ProjectRoleSkill projectRoleSkill = projectAccessService.getProjectRoleSkillForModification(userPrincipal, projectId, projectRoleId, skillId);

        if (request.getMinLevel() != null) {
            projectRoleSkill.setMinLevel(request.getMinLevel());
        }

        return mapper.toProjectRoleSkillView(projectRoleSkill);
    }

    public void removeRoleSkill(UserPrincipal userPrincipal, Long projectId, Long projectRoleId, Long skillId) {
        ProjectRoleSkill projectRoleSkill = projectAccessService.getProjectRoleSkillForModification(userPrincipal, projectId, projectRoleId, skillId);

        repository.delete(projectRoleSkill);
    }

}
