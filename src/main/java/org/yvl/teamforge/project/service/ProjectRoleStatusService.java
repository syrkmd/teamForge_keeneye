package org.yvl.teamforge.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.entity.enums.TeamMemberStatus;
import org.yvl.teamforge.repository.TeamMemberRepository;

@Service
@RequiredArgsConstructor
public class ProjectRoleStatusService {

    private final TeamMemberRepository teamMemberRepository;

    public void updateStatus(ProjectRole projectRole) {
        int activeMembers = teamMemberRepository.countByProjectRoleIdAndStatus(
                projectRole.getId(),
                TeamMemberStatus.ACTIVE
        );

        if (activeMembers >= projectRole.getRequiredCount()) {
            projectRole.setStatus(ProjectRoleStatus.CLOSED);
        } else {
            projectRole.setStatus(ProjectRoleStatus.OPEN);
        }
    }
}
