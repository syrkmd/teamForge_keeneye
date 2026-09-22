package org.yvl.teamforge.project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.entity.enums.TeamMemberStatus;
import org.yvl.teamforge.repository.TeamMemberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectRoleStatusServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ProjectRoleStatusService projectRoleStatusService;

    @Test
    void updateStatus_closesRoleWhenEnoughActiveMembers() {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(3);

        when(teamMemberRepository.countByProjectRoleIdAndStatus(
                projectRole.getId(),
                TeamMemberStatus.ACTIVE)
        ).thenReturn(3);

       projectRoleStatusService.updateStatus(projectRole);

       var expected = ProjectRoleStatus.CLOSED;

       assertEquals(expected, projectRole.getStatus());

       verify(teamMemberRepository).countByProjectRoleIdAndStatus(
               projectRole.getId(), TeamMemberStatus.ACTIVE
       );
    }

    @Test
    void updateStatus_opensRoleWhenNotEnoughActiveMembers() {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(3);

        when(teamMemberRepository.countByProjectRoleIdAndStatus(
                projectRole.getId(),
                TeamMemberStatus.ACTIVE)
        ).thenReturn(2);

        projectRoleStatusService.updateStatus(projectRole);

        var expected = ProjectRoleStatus.OPEN;

        assertEquals(expected, projectRole.getStatus());

        verify(teamMemberRepository).countByProjectRoleIdAndStatus(
                projectRole.getId(), TeamMemberStatus.ACTIVE
        );
    }
}