package org.yvl.authenticationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.authenticationservice.entity.SystemRole;
import org.yvl.authenticationservice.entity.enums.SystemRoleName;

import java.util.Optional;

public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {
    Optional<SystemRole> findByName(SystemRoleName name);
}
