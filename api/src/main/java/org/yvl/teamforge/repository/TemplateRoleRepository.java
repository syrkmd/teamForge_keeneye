package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.TemplateRole;

import java.util.List;

public interface TemplateRoleRepository extends JpaRepository<TemplateRole, Long> {

    List<TemplateRole> findByProjectTemplateId(Long templateId);
}
