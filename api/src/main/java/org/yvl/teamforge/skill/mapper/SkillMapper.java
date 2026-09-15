package org.yvl.teamforge.skill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yvl.teamforge.entity.Skill;
import org.yvl.teamforge.entity.SkillCategory;
import org.yvl.teamforge.entity.UserSkill;
import org.yvl.teamforge.skill.dto.response.SkillCategoryView;
import org.yvl.teamforge.skill.dto.response.SkillView;
import org.yvl.teamforge.skill.dto.response.UserSkillView;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(source = "category.name", target = "category")
    SkillView toSkillView(Skill skill);

    SkillCategoryView toSkillCategoryView(SkillCategory skillCategory);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "skillName")
    @Mapping(source = "skill.category.name", target = "category")
    @Mapping(source = "skill.type", target = "type")
    UserSkillView toUserSkillView(UserSkill userSkill);
}
