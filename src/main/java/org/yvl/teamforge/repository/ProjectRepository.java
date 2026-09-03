package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yvl.teamforge.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


}
