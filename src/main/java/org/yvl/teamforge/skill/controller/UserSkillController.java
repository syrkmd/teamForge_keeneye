package org.yvl.teamforge.skill.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.skill.dto.request.UserSkillRequest;
import org.yvl.teamforge.skill.dto.request.UserSkillUpdateRequest;
import org.yvl.teamforge.skill.dto.response.UserSkillView;
import org.yvl.teamforge.skill.service.UserSkillService;

@RestController
@RequestMapping("/user/skills")
@RequiredArgsConstructor
@Validated
public class UserSkillController {

    private final UserSkillService service;

    @GetMapping
    public Page<UserSkillView> getSkills(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") int size
    ) {
        return service.getSkills(userPrincipal, PageRequest.of(page, size));
    }

    @PostMapping
    public UserSkillView addSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserSkillRequest request
    ) {
        return service.addSkill(userPrincipal, request);
    }

    @PatchMapping("/{skillId}")
    public UserSkillView updateSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long skillId,
            @Valid @RequestBody UserSkillUpdateRequest request
    ) {
        return service.updateLevel(userPrincipal, skillId, request);
    }

    @DeleteMapping("/{skillId}")
    public void deleteSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long skillId
    ) {
        service.deleteSkill(userPrincipal, skillId);
    }
}
