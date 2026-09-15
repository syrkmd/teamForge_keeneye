package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.Team;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    
    Optional<Team> findByProjectId(Long projectId);
}
