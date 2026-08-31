package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.UserSkill;

import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    Page<UserSkill> findByUserId(Long userId, Pageable pageable);

    void deleteAllBySkillId(Long skillId);
}
