package org.yvl.teamforge.matching.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yvl.teamforge.entity.*;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.matching.dto.response.CandidateView;
import org.yvl.teamforge.matching.dto.response.MatchedSkillView;
import org.yvl.teamforge.matching.dto.response.ProjectRoleCandidatesView;
import org.yvl.teamforge.matching.exception.ProjectRoleHasNoSkillRequirementsException;
import org.yvl.teamforge.matching.mapper.MatchingMapper;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.repository.ProjectRoleRepository;
import org.yvl.teamforge.repository.ProjectRoleSkillRepository;
import org.yvl.teamforge.repository.UserRepository;
import org.yvl.teamforge.repository.UserSkillRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamMatchingServiceTest {

    @Mock
    private ProjectRoleSkillRepository projectRoleSkillRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRoleRepository projectRoleRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private UserPrincipal userPrincipal;

    @Mock
    private MatchingMapper matchingMapper;

    @Mock
    private MatchedSkillView matchedSkillView;

    @Mock
    private CandidateView candidateView;

    @Mock
    private ProjectRoleCandidatesView projectRoleCandidatesView;

    @Spy
    @InjectMocks
    private TeamMatchingService teamMatchingService;

    @Test
    void getCandidates_throwsWhenRoleHasNoSkillRequirements() {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);

        Long projectId = 1L;
        Long projectRoleId = 1L;

        when(projectAccessService.getProjectRoleForMatching(
                userPrincipal,
                projectId,
                projectRoleId
        )).thenReturn(projectRole);

        when(projectRoleSkillRepository.findByProjectRoleId(projectRole.getId()))
                .thenReturn(List.of());

        assertThrows(
                ProjectRoleHasNoSkillRequirementsException.class,
                () -> teamMatchingService.getCandidates(
                        userPrincipal,
                        projectId,
                        projectRoleId
                )
        );
    }

    @Test
    void getCandidates_excludesProjectOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setAverageRating(5.0);
        owner.setCompletionRate(1.0);

        User user1 = new User();
        user1.setId(2L);
        user1.setAverageRating(4.5);
        user1.setCompletionRate(0.8);

        User user2 = new User();
        user2.setId(3L);
        user2.setAverageRating(4.0);
        user2.setCompletionRate(0.7);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(2);
        projectRole.setProject(project);

        Long projectId = 1L;
        Long projectRoleId = 1L;

        when(projectAccessService.getProjectRoleForMatching(
                userPrincipal,
                projectId,
                projectRoleId
        )).thenReturn(projectRole);

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        ProjectRoleSkill projectRoleSkill = new ProjectRoleSkill();
        projectRoleSkill.setSkill(skill);
        projectRoleSkill.setMinLevel(3);
        projectRoleSkill.setProjectRole(projectRole);

        when(projectRoleSkillRepository.findByProjectRoleId(projectRole.getId()))
                .thenReturn(List.of(projectRoleSkill));

        List<Long> userIds = List.of(
                owner.getId(),
                user1.getId(),
                user2.getId()
        );

        when(userSkillRepository.findMatchingUserIds(projectRoleId))
                .thenReturn(userIds);

        when(userRepository.findAllById(userIds)
        ).thenReturn(List.of(owner, user1, user2));

        UserSkill userSkill1 = new UserSkill();
        userSkill1.setUser(user1);
        userSkill1.setSkill(skill);
        userSkill1.setLevel(4);

        UserSkill userSkill2 = new UserSkill();
        userSkill2.setUser(user2);
        userSkill2.setSkill(skill);
        userSkill2.setLevel(3);

        when(userSkillRepository.findByUserIdInAndSkillIdIn(
                userIds,
                List.of(skill.getId())
        )).thenReturn(List.of(userSkill1, userSkill2));

        when(matchingMapper.toMatchedSkillView(
                anyLong(),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(matchedSkillView);

        when(matchingMapper.toCandidateView(
                anyLong(),
                anyDouble(),
                anyDouble(),
                anyList()
        )).thenReturn(candidateView);

        when(matchingMapper.toProjectRoleCandidatesView(
                anyLong(),
                anyInt(),
                anyBoolean(),
                anyList()
        )).thenReturn(projectRoleCandidatesView);

        teamMatchingService.getCandidates(
                userPrincipal,
                projectId,
                projectRoleId
        );

        verify(matchingMapper).toCandidateView(
                eq(user1.getId()),
                eq(user1.getAverageRating()),
                eq(user1.getCompletionRate()),
                anyList()
        );

        verify(matchingMapper).toCandidateView(
                eq(user2.getId()),
                eq(user2.getAverageRating()),
                eq(user2.getCompletionRate()),
                anyList()
        );

        verify(matchingMapper, never()).toCandidateView(
                eq(owner.getId()),
                anyDouble(),
                anyDouble(),
                anyList()
        );
    }

    @Test
    void getCandidates_sortsCandidatesByRatingCompletionRateAndId() {
        User owner = new User();
        owner.setId(1L);
        owner.setAverageRating(5.0);
        owner.setCompletionRate(1.0);

        User user1 = new User();
        user1.setId(2L);
        user1.setAverageRating(4.5);
        user1.setCompletionRate(0.8);

        User user2 = new User();
        user2.setId(3L);
        user2.setAverageRating(5.0);
        user2.setCompletionRate(0.7);

        User user3 = new User();
        user3.setId(4L);
        user3.setAverageRating(4.5);
        user3.setCompletionRate(0.9);

        User user4 = new User();
        user4.setId(5L);
        user4.setAverageRating(4.5);
        user4.setCompletionRate(0.9);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(4);
        projectRole.setProject(project);

        Long projectId = 1L;
        Long projectRoleId = 1L;

        when(projectAccessService.getProjectRoleForMatching(
                userPrincipal,
                projectId,
                projectRoleId
        )).thenReturn(projectRole);

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        ProjectRoleSkill projectRoleSkill = new ProjectRoleSkill();
        projectRoleSkill.setSkill(skill);
        projectRoleSkill.setMinLevel(3);
        projectRoleSkill.setProjectRole(projectRole);

        when(projectRoleSkillRepository.findByProjectRoleId(projectRoleId))
                .thenReturn(List.of(projectRoleSkill));

        List<Long> userIds = List.of(
                user1.getId(),
                user2.getId(),
                user3.getId(),
                user4.getId()
        );

        when(userSkillRepository.findMatchingUserIds(projectRoleId))
                .thenReturn(userIds);


        when(userRepository.findAllById(userIds)
        ).thenReturn(List.of(
                user1,
                user2,
                user3,
                user4
        ));

        UserSkill userSkill1 = new UserSkill();
        userSkill1.setUser(user1);
        userSkill1.setSkill(skill);
        userSkill1.setLevel(4);

        UserSkill userSkill2 = new UserSkill();
        userSkill2.setUser(user2);
        userSkill2.setSkill(skill);
        userSkill2.setLevel(3);

        UserSkill userSkill3 = new UserSkill();
        userSkill3.setUser(user3);
        userSkill3.setSkill(skill);
        userSkill3.setLevel(5);

        UserSkill userSkill4 = new UserSkill();
        userSkill4.setUser(user4);
        userSkill4.setSkill(skill);
        userSkill4.setLevel(3);

        when(userSkillRepository.findByUserIdInAndSkillIdIn(
                userIds,
                List.of(skill.getId())
        )).thenReturn(List.of(
                userSkill1,
                userSkill2,
                userSkill3,
                userSkill4
        ));

        when(matchingMapper.toMatchedSkillView(
                anyLong(),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(matchedSkillView);

        when(matchingMapper.toProjectRoleCandidatesView(
                anyLong(),
                anyInt(),
                anyBoolean(),
                anyList()
        )).thenReturn(projectRoleCandidatesView);

        CandidateView candidateView1 = mock(CandidateView.class);
        CandidateView candidateView2 = mock(CandidateView.class);
        CandidateView candidateView3 = mock(CandidateView.class);
        CandidateView candidateView4 = mock(CandidateView.class);

        when(matchingMapper.toCandidateView(
                eq(user1.getId()),
                anyDouble(),
                anyDouble(),
                anyList()
        )).thenReturn(candidateView1);

        when(matchingMapper.toCandidateView(
                eq(user2.getId()),
                anyDouble(),
                anyDouble(),
                anyList()
        )).thenReturn(candidateView2);

        when(matchingMapper.toCandidateView(
                eq(user3.getId()),
                anyDouble(),
                anyDouble(),
                anyList()
        )).thenReturn(candidateView3);

        when(matchingMapper.toCandidateView(
                eq(user4.getId()),
                anyDouble(),
                anyDouble(),
                anyList()
        )).thenReturn(candidateView4);

        teamMatchingService.getCandidates(
                userPrincipal,
                projectId,
                projectRoleId
        );

        verify(matchingMapper).toProjectRoleCandidatesView(
                eq(projectRoleId),
                eq(projectRole.getRequiredCount()),
                eq(true),
                argThat(candidates ->
                        candidates.size() == 4
                                && candidates.getFirst() == candidateView2
                                && candidates.get(1) == candidateView3
                                && candidates.get(2) == candidateView4
                                && candidates.get(3) == candidateView1
                )
        );
    }

    @Test
    void getCandidates_limitsNumberOfCandidates() {
        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(3);
        projectRole.setProject(project);

        Long projectId = 1L;
        Long projectRoleId = 1L;

        when(projectAccessService.getProjectRoleForMatching(
                userPrincipal,
                projectId,
                projectRoleId
        )).thenReturn(projectRole);

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        ProjectRoleSkill projectRoleSkill = new ProjectRoleSkill();
        projectRoleSkill.setSkill(skill);
        projectRoleSkill.setMinLevel(3);
        projectRoleSkill.setProjectRole(projectRole);

        when(projectRoleSkillRepository.findByProjectRoleId(projectRoleId))
                .thenReturn(List.of(projectRoleSkill));

        User user1 = new User();
        user1.setId(2L);
        user1.setAverageRating(4.0);
        user1.setCompletionRate(0.8);

        User user2 = new User();
        user2.setId(3L);
        user2.setAverageRating(4.0);
        user2.setCompletionRate(0.8);

        User user3 = new User();
        user3.setId(4L);
        user3.setAverageRating(4.0);
        user3.setCompletionRate(0.8);

        User user4 = new User();
        user4.setId(5L);
        user4.setAverageRating(4.0);
        user4.setCompletionRate(0.8);

        User user5 = new User();
        user5.setId(6L);
        user5.setAverageRating(4.0);
        user5.setCompletionRate(0.8);

        User user6 = new User();
        user6.setId(7L);
        user6.setAverageRating(4.0);
        user6.setCompletionRate(0.8);

        User user7 = new User();
        user7.setId(8L);
        user7.setAverageRating(4.0);
        user7.setCompletionRate(0.8);

        List<Long> userIds = List.of(
                user1.getId(),
                user2.getId(),
                user3.getId(),
                user4.getId(),
                user5.getId(),
                user6.getId(),
                user7.getId()
        );

        when(userSkillRepository.findMatchingUserIds(projectRoleId))
                .thenReturn(userIds);

        when(userRepository.findAllById(userIds))
                .thenReturn(List.of(
                        user1,
                        user2,
                        user3,
                        user4,
                        user5,
                        user6,
                        user7
                ));

        UserSkill userSkill1 = new UserSkill();
        userSkill1.setUser(user1);
        userSkill1.setSkill(skill);
        userSkill1.setLevel(3);

        UserSkill userSkill2 = new UserSkill();
        userSkill2.setUser(user2);
        userSkill2.setSkill(skill);
        userSkill2.setLevel(3);

        UserSkill userSkill3 = new UserSkill();
        userSkill3.setUser(user3);
        userSkill3.setSkill(skill);
        userSkill3.setLevel(3);

        UserSkill userSkill4 = new UserSkill();
        userSkill4.setUser(user4);
        userSkill4.setSkill(skill);
        userSkill4.setLevel(3);

        UserSkill userSkill5 = new UserSkill();
        userSkill5.setUser(user5);
        userSkill5.setSkill(skill);
        userSkill5.setLevel(3);

        UserSkill userSkill6 = new UserSkill();
        userSkill6.setUser(user6);
        userSkill6.setSkill(skill);
        userSkill6.setLevel(3);

        UserSkill userSkill7 = new UserSkill();
        userSkill7.setUser(user7);
        userSkill7.setSkill(skill);
        userSkill7.setLevel(3);

        when(userSkillRepository.findByUserIdInAndSkillIdIn(
                userIds,
                List.of(skill.getId())
        )).thenReturn(List.of(
                userSkill1,
                userSkill2,
                userSkill3,
                userSkill4,
                userSkill5,
                userSkill6,
                userSkill7
        ));

        when(matchingMapper.toMatchedSkillView(
                anyLong(),
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(matchedSkillView);

        CandidateView candidateView1 = mock(CandidateView.class);
        CandidateView candidateView2 = mock(CandidateView.class);
        CandidateView candidateView3 = mock(CandidateView.class);
        CandidateView candidateView4 = mock(CandidateView.class);
        CandidateView candidateView5 = mock(CandidateView.class);

        when(matchingMapper.toCandidateView(
                eq(user1.getId()),
                eq(user1.getAverageRating()),
                eq(user1.getCompletionRate()),
                anyList()
        )).thenReturn(candidateView1);

        when(matchingMapper.toCandidateView(
                eq(user2.getId()),
                eq(user2.getAverageRating()),
                eq(user2.getCompletionRate()),
                anyList()
        )).thenReturn(candidateView2);

        when(matchingMapper.toCandidateView(
                eq(user3.getId()),
                eq(user3.getAverageRating()),
                eq(user3.getCompletionRate()),
                anyList()
        )).thenReturn(candidateView3);

        when(matchingMapper.toCandidateView(
                eq(user4.getId()),
                eq(user4.getAverageRating()),
                eq(user4.getCompletionRate()),
                anyList()
        )).thenReturn(candidateView4);

        when(matchingMapper.toCandidateView(
                eq(user5.getId()),
                eq(user5.getAverageRating()),
                eq(user5.getCompletionRate()),
                anyList()
        )).thenReturn(candidateView5);

        teamMatchingService.getCandidates(
                userPrincipal,
                projectId,
                projectRoleId
        );

        verify(matchingMapper).toProjectRoleCandidatesView(
                eq(projectRoleId),
                eq(projectRole.getRequiredCount()),
                eq(true),
                argThat(candidates ->
                        candidates.size() == 5 &&
                                candidates.getFirst() == candidateView1 &&
                                candidates.get(1) == candidateView2 &&
                                candidates.get(2) == candidateView3 &&
                                candidates.get(3) == candidateView4 &&
                                candidates.get(4) == candidateView5
                )
        );
    }

    @Test
    void getCandidates_returnsMatchedSkills() {
        User owner = new User();
        owner.setId(1L);

        User candidate = new User();
        candidate.setId(2L);
        candidate.setAverageRating(4.5);
        candidate.setCompletionRate(0.8);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(1);
        projectRole.setProject(project);

        Long projectId = 1L;
        Long projectRoleId = 1L;

        when(projectAccessService.getProjectRoleForMatching(
                userPrincipal,
                projectId,
                projectRoleId
        )).thenReturn(projectRole);

        Skill skill = new Skill();
        skill.setId(10L);
        skill.setName("Java");

        ProjectRoleSkill projectRoleSkill = new ProjectRoleSkill();
        projectRoleSkill.setSkill(skill);
        projectRoleSkill.setMinLevel(3);
        projectRoleSkill.setProjectRole(projectRole);

        when(projectRoleSkillRepository.findByProjectRoleId(projectRoleId))
                .thenReturn(List.of(projectRoleSkill));

        when(userSkillRepository.findMatchingUserIds(projectRoleId))
                .thenReturn(List.of(candidate.getId()));

        when(userRepository.findAllById(List.of(candidate.getId())))
                .thenReturn(List.of(candidate));

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(candidate);
        userSkill.setSkill(skill);
        userSkill.setLevel(5);

        when(userSkillRepository.findByUserIdInAndSkillIdIn(
                List.of(candidate.getId()),
                List.of(skill.getId())
        )).thenReturn(List.of(userSkill));

        when(matchingMapper.toMatchedSkillView(
                skill.getId(),
                skill.getName(),
                userSkill.getLevel(),
                projectRoleSkill.getMinLevel()
        )).thenReturn(matchedSkillView);

        when(matchingMapper.toCandidateView(
                eq(candidate.getId()),
                eq(candidate.getAverageRating()),
                eq(candidate.getCompletionRate()),
                anyList()
        )).thenReturn(candidateView);

        when(matchingMapper.toProjectRoleCandidatesView(
                eq(projectRoleId),
                eq(projectRole.getRequiredCount()),
                anyBoolean(),
                anyList()
        )).thenReturn(projectRoleCandidatesView);

        teamMatchingService.getCandidates(
                userPrincipal,
                projectId,
                projectRoleId
        );

        verify(matchingMapper).toMatchedSkillView(
                eq(skill.getId()),
                eq(skill.getName()),
                eq(userSkill.getLevel()),
                eq(projectRoleSkill.getMinLevel())
        );
    }

    @Test
    void getCandidates_returnsNotEnoughCandidatesWhenNoMatches() {
        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        ProjectRole projectRole = new ProjectRole();
        projectRole.setId(1L);
        projectRole.setRequiredCount(3);
        projectRole.setProject(project);

        Long projectId = 1L;
        Long projectRoleId = 1L;

        when(projectAccessService.getProjectRoleForMatching(
                userPrincipal,
                projectId,
                projectRoleId
        )).thenReturn(projectRole);

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        ProjectRoleSkill projectRoleSkill = new ProjectRoleSkill();
        projectRoleSkill.setMinLevel(3);
        projectRoleSkill.setProjectRole(projectRole);
        projectRoleSkill.setSkill(skill);

        when(projectRoleSkillRepository.findByProjectRoleId(projectRoleId))
                .thenReturn(List.of(projectRoleSkill));

        when(userSkillRepository.findMatchingUserIds(projectRoleId))
                .thenReturn(List.of());

        when(userRepository.findAllById(List.of()))
                .thenReturn(List.of());

        when(userSkillRepository.findByUserIdInAndSkillIdIn(
                List.of(),
                List.of(skill.getId())
        )).thenReturn(List.of());

        teamMatchingService.getCandidates(
                userPrincipal,
                projectId,
                projectRoleId
        );

        verify(matchingMapper).toProjectRoleCandidatesView(
                eq(projectRoleId),
                eq(projectRole.getRequiredCount()),
                eq(false),
                argThat(List::isEmpty)
        );
    }

    @Test
    void getCandidatesForProject_skipsRolesWithoutSkillRequirements() {
        Project project = new Project();
        project.setId(1L);

        when(projectAccessService.getProjectForModification(
                userPrincipal,
                project.getId()
        )).thenReturn(project);

        ProjectRole projectRole1 = new ProjectRole();
        projectRole1.setId(1L);
        projectRole1.setRequiredCount(1);
        projectRole1.setProject(project);
        projectRole1.setStatus(ProjectRoleStatus.OPEN);

        ProjectRole projectRole2 = new ProjectRole();
        projectRole2.setId(2L);
        projectRole2.setRequiredCount(1);
        projectRole2.setProject(project);
        projectRole2.setStatus(ProjectRoleStatus.OPEN);

        when(projectRoleRepository.findByProjectIdAndStatus(
                project.getId(),
                ProjectRoleStatus.OPEN
        )).thenReturn(List.of(projectRole1, projectRole2));

        ProjectRoleCandidatesView role1Result = projectRoleCandidatesView;

        doReturn(role1Result)
                .when(teamMatchingService).getCandidates(
                        userPrincipal,
                        project.getId(),
                        projectRole1.getId()
                );

        doThrow(new ProjectRoleHasNoSkillRequirementsException(projectRole2.getId()))
                .when(teamMatchingService).getCandidates(
                        userPrincipal,
                        project.getId(),
                        projectRole2.getId()
                );

        var result =
                teamMatchingService.getCandidatesForProject(
                        userPrincipal,
                        project.getId()
                );

        assertEquals(1, result.size());
        assertSame(role1Result, result.getFirst());

        verify(teamMatchingService).getCandidates(
                userPrincipal,
                project.getId(),
                projectRole1.getId()
        );

        verify(teamMatchingService).getCandidates(
                userPrincipal,
                project.getId(),
                projectRole2.getId()
        );
    }
}