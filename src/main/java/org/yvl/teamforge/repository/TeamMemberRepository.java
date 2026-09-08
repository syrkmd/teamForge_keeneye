package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.TeamMember;
import org.yvl.teamforge.entity.enums.TeamMemberStatus;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeamIdAndUserIdAndProjectRoleIdAndStatus(Long teamId, Long userId, Long projectRoleId, TeamMemberStatus status);

    int countByProjectRoleIdAndStatus(Long projectRoleId, TeamMemberStatus status);

    Page<TeamMember> findByTeamId(Long teamId, Pageable pageable);

    void deleteAllByProjectRoleId(Long projectRoleId);
}
