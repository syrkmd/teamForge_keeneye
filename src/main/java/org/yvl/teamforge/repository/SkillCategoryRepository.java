package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.SkillCategory;
import org.yvl.teamforge.entity.enums.SkillCategoryName;

import java.util.Optional;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    Optional<SkillCategory> findByName(SkillCategoryName name);
}
