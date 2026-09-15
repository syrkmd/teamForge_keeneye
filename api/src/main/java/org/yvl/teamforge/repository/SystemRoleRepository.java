package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.SystemRole;
import org.yvl.teamforge.entity.enums.SystemRoleName;

import java.util.Optional;

public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {
    Optional<SystemRole> findByName(SystemRoleName name);
}
