package org.yvl.teamforge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yvl.teamforge.entity.TeamMember;
import org.yvl.teamforge.entity.enums.TeamMemberStatus;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeamIdAndUserIdAndProjectRoleIdAndStatus(Long teamId, Long userId, Long projectRoleId, TeamMemberStatus status);

    Optional<TeamMember> findByIdAndUserIdAndTeam_Project_Id(Long id, Long userId, Long projectId);

    int countByProjectRoleIdAndStatus(Long projectRoleId, TeamMemberStatus status);

    boolean existsByProjectRoleId(Long projectRoleId);

    Page<TeamMember> findByTeamId(Long teamId, Pageable pageable);

    Optional<TeamMember> findByIdAndTeam_Project_Id(Long id, Long projectId);

    List<TeamMember> findByTeamIdAndStatus(Long teamId, TeamMemberStatus status);
}
