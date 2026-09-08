package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.Invitation;
import org.yvl.teamforge.entity.enums.InvitationStatus;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Page<Invitation> findByProjectRoleId(Long projectRoleId, Pageable pageable);

    boolean existsByProjectRoleIdAndUserIdAndStatus(Long projectRoleId, Long userId, InvitationStatus status);

    Page<Invitation> findByUserId(Long userId, Pageable pageable);

    void deleteAllByProjectRoleId(Long projectRoleId);
}
