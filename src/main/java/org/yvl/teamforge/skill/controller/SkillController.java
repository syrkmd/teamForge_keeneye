package org.yvl.teamforge.skill.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.skill.dto.request.SkillCreateRequest;
import org.yvl.teamforge.skill.dto.response.SkillCategoryView;
import org.yvl.teamforge.skill.dto.response.SkillView;
import org.yvl.teamforge.skill.service.SkillService;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@Validated
public class SkillController {

    private final SkillService service;

    @GetMapping("/category")
    public Page<SkillCategoryView> getSkillCategories(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size
    ) {
        return service.getSkillCategories(PageRequest.of(page, size));
    }

    @GetMapping
    public Page<SkillView> getSkills(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size
    ) {
        return service.getSkills(PageRequest.of(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public SkillView createSkill(
            @Valid @RequestBody SkillCreateRequest request
    ) {
        return service.createSkill(request);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{skillId}")
    public void deleteSkill(
            @PathVariable Long skillId
    ) {
        service.deleteSkill(skillId);
    }
}
