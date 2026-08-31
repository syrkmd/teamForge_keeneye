package org.yvl.teamforge.skill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.Skill;
import org.yvl.teamforge.entity.SkillCategory;
import org.yvl.teamforge.entity.enums.TypeSkill;
import org.yvl.teamforge.exception.SkillAlreadyExistsException;
import org.yvl.teamforge.exception.SkillCategoryNotFoundException;
import org.yvl.teamforge.exception.SkillNotFoundException;
import org.yvl.teamforge.repository.SkillCategoryRepository;
import org.yvl.teamforge.repository.SkillRepository;
import org.yvl.teamforge.repository.UserSkillRepository;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.skill.dto.request.SkillCreateRequest;
import org.yvl.teamforge.skill.dto.response.SkillCategoryView;
import org.yvl.teamforge.skill.dto.response.SkillView;
import org.yvl.teamforge.skill.mapper.SkillMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {

    private final SkillRepository repository;
    private final SkillCategoryRepository categoryRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillMapper skillMapper;

    public Page<SkillView> getSkills(Pageable pageable) {
        Page<Skill> skills = repository.findAll(pageable);

        return skills.map(skillMapper::toSkillView);
    }

    public Page<SkillCategoryView> getSkillCategories(Pageable pageable) {
        Page<SkillCategory> categories = categoryRepository.findAll(pageable);

        return categories.map(skillMapper::toSkillCategoryView);
    }

    public SkillView createSkill(SkillCreateRequest request) {

        SkillCategory category = categoryRepository.findByName(request.getCategory()).orElseThrow(() -> new SkillCategoryNotFoundException(request.getCategory()));

        if (repository.existsByCategoryAndName(category, request.getName())) {
            throw new SkillAlreadyExistsException(category.getName(), request.getName());
        }

        Skill skill = repository.save(
                Skill.builder()
                        .name(request.getName())
                        .category(category)
                        .type(TypeSkill.GLOBAL)
                        .build()
        );

        return skillMapper.toSkillView(skill);
    }

    public void deleteSkill(Long skillId) {
        Skill skill = repository.findById(skillId).orElseThrow(() ->
                new SkillNotFoundException(skillId));

        userSkillRepository.deleteAllBySkillId(skillId);

        repository.delete(skill);
    }
}
