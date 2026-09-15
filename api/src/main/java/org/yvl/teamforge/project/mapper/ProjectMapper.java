package org.yvl.teamforge.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yvl.teamforge.entity.Project;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.ProjectRoleSkill;
import org.yvl.teamforge.entity.ProjectTemplate;
import org.yvl.teamforge.project.dto.response.ProjectRoleSkillView;
import org.yvl.teamforge.project.dto.response.ProjectRoleView;
import org.yvl.teamforge.project.dto.response.ProjectTemplateView;
import org.yvl.teamforge.project.dto.response.ProjectView;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectTemplateView toProjectTemplateView(ProjectTemplate projectTemplate);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "template.id", target = "templateId")
    ProjectView toProjectView(Project project);

    ProjectRoleView toProjectRoleView(ProjectRole projectRole);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "skillName")
    @Mapping(source = "skill.category.name", target = "category")
    @Mapping(source = "skill.type", target = "type")
    ProjectRoleSkillView toProjectRoleSkillView(ProjectRoleSkill projectRoleSkill);
}
