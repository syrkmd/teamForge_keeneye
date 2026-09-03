package org.yvl.teamforge.project.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.project.dto.response.ProjectTemplateView;
import org.yvl.teamforge.project.service.ProjectTemplateService;

@Validated
@RestController
@RequestMapping("/project-templates")
@RequiredArgsConstructor
public class ProjectTemplateController {

    private final ProjectTemplateService service;

    @GetMapping
    public Page<ProjectTemplateView> getActiveTemplates(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size
    ) {
        return service.getActiveTemplates(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ProjectTemplateView getTemplateByIdAndIsActiveTrue(
            @PathVariable Long id
    ) {
        return service.getTemplateById(id);
    }
}
