package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yvl.teamforge.analytics.dto.projection.SkillGapRow;
import org.yvl.teamforge.entity.ProjectRoleSkill;

import java.util.List;
import java.util.Optional;

public interface ProjectRoleSkillRepository extends JpaRepository<ProjectRoleSkill, Long> {

    Page<ProjectRoleSkill> findByProjectRoleId(Long projectRoleId, Pageable pageable);

    List<ProjectRoleSkill> findByProjectRoleId(Long projectRoleId);

    boolean existsByProjectRoleIdAndSkillId(Long projectRoleId, Long skillId);

    Optional<ProjectRoleSkill> findByProjectRoleIdAndSkillId(Long projectRoleId, Long skillId);

    void deleteAllByProjectRoleId(Long projectRoleId);

    @Query(
            value = """
                SELECT pr.role_name AS "roleName",
                       s.name AS "skillName",
                       prs.min_level AS "minLevel",
                       COALESCE(MAX(us.level), 0) AS "actualLevel"
                FROM project_role_skills prs
                JOIN project_roles pr ON pr.id = prs.project_role_id
                JOIN teams t ON t.project_id = pr.project_id
                JOIN skills s ON s.id = prs.skill_id
                LEFT JOIN team_members tm ON tm.project_role_id = pr.id
                    AND tm.team_id = t.id
                    AND tm.status = 'ACTIVE'
                LEFT JOIN user_skills us ON us.user_id = tm.user_id
                    AND us.skill_id = prs.skill_id
                WHERE t.project_id = :projectId
                GROUP BY pr.id, s.id, pr.role_name, s.name, prs.min_level
                HAVING COALESCE(MAX(us.level), 0) < prs.min_level
                ORDER BY pr.id, s.id
                """,
            nativeQuery = true
    )
    List<SkillGapRow> findSkillGapRowsByProjectId(@Param("projectId") Long projectId);
}