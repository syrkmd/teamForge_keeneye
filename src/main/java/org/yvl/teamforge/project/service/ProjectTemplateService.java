package org.yvl.teamforge.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.yvl.teamforge.entity.ProjectTemplate;
import org.yvl.teamforge.exception.ProjectTemplateNotFoundException;
import org.yvl.teamforge.project.dto.response.ProjectTemplateView;
import org.yvl.teamforge.project.mapper.ProjectMapper;
import org.yvl.teamforge.repository.ProjectTemplateRepository;

@Service
@RequiredArgsConstructor
public class ProjectTemplateService {

    private final ProjectTemplateRepository repository;
    private final ProjectMapper mapper;

    public Page<ProjectTemplateView> getActiveTemplates(Pageable pageable) {
        Page<ProjectTemplate> projectTemplates = repository.findAllByIsActiveTrue(pageable);

        return projectTemplates.map(mapper::toProjectTemplateView);
    }

    public ProjectTemplateView getTemplateById(Long id) {
        return repository.findByIdAndIsActiveTrue(id).map(mapper::toProjectTemplateView).orElseThrow(() ->
                new ProjectTemplateNotFoundException(id));
    }
}
