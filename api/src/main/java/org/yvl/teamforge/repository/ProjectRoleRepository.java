package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.TemplateRoleSkill;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;

import java.util.List;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Long> {

    Page<ProjectRole> findByProjectId(Long projectId, Pageable pageable);

    List<ProjectRole> findByProjectIdAndStatus(Long projectId, ProjectRoleStatus status);

    boolean existsByProjectIdAndStatus(Long projectId, ProjectRoleStatus status);
}
