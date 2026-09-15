package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.Skill;
import org.yvl.teamforge.entity.SkillCategory;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByCategoryAndName(SkillCategory category, String name);

    Optional<Skill> findByCategoryAndName(SkillCategory category, String name);
}
