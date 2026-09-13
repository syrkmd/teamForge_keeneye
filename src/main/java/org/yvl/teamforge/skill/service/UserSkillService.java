package org.yvl.teamforge.skill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.Skill;
import org.yvl.teamforge.entity.SkillCategory;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.entity.UserSkill;
import org.yvl.teamforge.entity.enums.TypeSkill;
import org.yvl.teamforge.skill.exception.SkillCategoryNotFoundException;
import org.yvl.teamforge.skill.exception.SkillNotFoundException;
import org.yvl.teamforge.skill.exception.UserSkillAlreadyExistsException;
import org.yvl.teamforge.skill.exception.UserSkillNotFoundException;
import org.yvl.teamforge.repository.SkillCategoryRepository;
import org.yvl.teamforge.repository.SkillRepository;
import org.yvl.teamforge.repository.UserSkillRepository;
import org.yvl.teamforge.security.user.UserPrincipal;
import org.yvl.teamforge.skill.dto.request.UserSkillRequest;
import org.yvl.teamforge.skill.dto.request.UserSkillUpdateRequest;
import org.yvl.teamforge.skill.dto.response.UserSkillView;
import org.yvl.teamforge.skill.mapper.SkillMapper;

import java.time.Instant;


@Service
@RequiredArgsConstructor
@Transactional
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final SkillMapper skillMapper;

    public Page<UserSkillView> getSkills(UserPrincipal userPrincipal, Pageable pageable) {

        Page<UserSkill> userSkills = userSkillRepository.findByUserId(
                userPrincipal.getUser().getId(),
                pageable
        );

        return userSkills.map(skillMapper::toUserSkillView);
    }

    public UserSkillView addSkill(UserPrincipal userPrincipal, UserSkillRequest request) {

        User user = userPrincipal.getUser();

        Skill skill = request.getSkillId() != null
                ? getExistingSkill(user, request.getSkillId())
                : createCustomSkill(user, request);


        UserSkill userSkill = userSkillRepository.save(
                UserSkill.builder()
                        .user(user)
                        .level(request.getLevel())
                        .skill(skill)
                        .build()
        );

        return skillMapper.toUserSkillView(userSkill);
    }

    public UserSkillView updateLevel(UserPrincipal userPrincipal, Long skillId, UserSkillUpdateRequest request) {
        User user = userPrincipal.getUser();

        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(user.getId(), skillId).orElseThrow(() ->
                new UserSkillNotFoundException(skillId));

        userSkill.setLevel(request.getLevel());
        userSkill.setUpdatedAt(Instant.now());

        return skillMapper.toUserSkillView(userSkill);
    }

    public void deleteSkill(UserPrincipal userPrincipal, Long skillId) {
        User user = userPrincipal.getUser();

        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(user.getId(), skillId).orElseThrow(() ->
                new UserSkillNotFoundException(skillId));

        userSkillRepository.delete(userSkill);
    }

    private Skill getExistingSkill(User user, Long skillId) {
        if (userSkillRepository.existsByUserIdAndSkillId(user.getId(), skillId)) {
            throw new UserSkillAlreadyExistsException(skillId);
        }

        return skillRepository.findById(skillId).orElseThrow(() ->
                new SkillNotFoundException(skillId));
    }

    private Skill createCustomSkill(User user, UserSkillRequest request) {
        SkillCategory skillCategory = skillCategoryRepository.findByName(request.getCategory()).orElseThrow(() ->
                new SkillCategoryNotFoundException(request.getCategory()));

        return skillRepository.findByCategoryAndName(skillCategory, request.getSkillName())
                .orElseGet(() -> skillRepository.save(
                        Skill.builder()
                                .name(request.getSkillName())
                                .category(skillCategory)
                                .type(TypeSkill.CUSTOM)
                                .createdByUser(user)
                                .build()
                ));
    }
}
