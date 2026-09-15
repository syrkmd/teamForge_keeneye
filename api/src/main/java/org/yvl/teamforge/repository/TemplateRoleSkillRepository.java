package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.TemplateRoleSkill;

import java.util.List;

public interface TemplateRoleSkillRepository extends JpaRepository<TemplateRoleSkill, Long> {

    List<TemplateRoleSkill> findByTemplateRoleIdIn(List<Long> templateRoleIds);
}
