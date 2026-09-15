package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yvl.teamforge.entity.ProjectTemplate;

import java.util.Optional;

@Repository
public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate, Long> {

    Optional<ProjectTemplate> findByIdAndIsActiveTrue(Long id);

    Page<ProjectTemplate> findAllByIsActiveTrue(Pageable pageable);
}
