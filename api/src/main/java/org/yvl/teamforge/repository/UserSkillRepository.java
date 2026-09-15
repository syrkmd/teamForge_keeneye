package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yvl.teamforge.entity.UserSkill;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    Page<UserSkill> findByUserId(Long userId, Pageable pageable);

    List<UserSkill> findBySkillIdIn(List<Long> skillIds);

    @Query("""
            SELECT us.user.id FROM UserSkill us
            JOIN ProjectRoleSkill prs ON prs.skill = us.skill
            WHERE prs.projectRole.id = :projectRoleId
            AND us.level >= prs.minLevel
            GROUP BY us.user.id
            HAVING COUNT(DISTINCT us.skill.id) = (
                        SELECT COUNT(pr.skill.id) FROM ProjectRoleSkill pr
                        WHERE pr.projectRole.id = :projectRoleId
            )
            """)
    List<Long> findMatchingUserIds(@Param("projectRoleId") Long projectRoleId);

    List<UserSkill> findByUserIdInAndSkillIdIn(List<Long> userId, List<Long> skillIds);
}
