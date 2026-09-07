package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.ProjectRoleSkill;

import java.util.List;
import java.util.Optional;

public interface ProjectRoleSkillRepository extends JpaRepository<ProjectRoleSkill, Long> {

    Page<ProjectRoleSkill> findByProjectRoleId(Long projectRoleId, Pageable pageable);

    List<ProjectRoleSkill> findByProjectRoleId(Long projectRoleId);

    boolean existsByProjectRoleIdAndSkillId(Long projectRoleId, Long skillId);

    Optional<ProjectRoleSkill> findByProjectRoleIdAndSkillId(Long projectRoleId, Long skillId);

    void deleteAllByProjectRoleId(Long projectRoleId);
}
