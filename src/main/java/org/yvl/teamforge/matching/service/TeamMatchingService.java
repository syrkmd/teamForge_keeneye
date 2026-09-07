package org.yvl.teamforge.matching.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.ProjectRole;
import org.yvl.teamforge.entity.ProjectRoleSkill;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.entity.UserSkill;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;
import org.yvl.teamforge.exception.ProjectRoleHasNoSkillRequirementsException;
import org.yvl.teamforge.matching.dto.response.CandidateView;
import org.yvl.teamforge.matching.dto.response.MatchedSkillView;
import org.yvl.teamforge.matching.dto.response.ProjectRoleCandidatesView;
import org.yvl.teamforge.matching.mapper.MatchingMapper;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.repository.ProjectRoleRepository;
import org.yvl.teamforge.repository.ProjectRoleSkillRepository;
import org.yvl.teamforge.repository.UserRepository;
import org.yvl.teamforge.repository.UserSkillRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMatchingService {

    private final MatchingMapper mapper;
    private final ProjectRoleSkillRepository projectRoleSkillRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProjectAccessService projectAccessService;
    private final UserRepository userRepository;
    private final ProjectRoleRepository projectRoleRepository;

    public ProjectRoleCandidatesView getCandidates(
            UserPrincipal userPrincipal,
            Long projectId,
            Long projectRoleId
    ) {
        ProjectRole projectRole = projectAccessService.getProjectRoleForMatching(userPrincipal, projectId, projectRoleId);

        List<ProjectRoleSkill> projectRoleSkills = projectRoleSkillRepository.findByProjectRoleId(projectRoleId);

        if (projectRoleSkills.isEmpty()) {
            throw new ProjectRoleHasNoSkillRequirementsException(projectRoleId);
        }

        List<Long> matchingUserIds = userSkillRepository.findMatchingUserIds(projectRoleId);

        List<User> matchingUsers = userRepository.findAllById(matchingUserIds);

        List<UserSkill> userSkills = userSkillRepository.findByUserIdInAndSkillIdIn(
                matchingUserIds,
                projectRoleSkills.stream()
                        .map(prs -> prs.getSkill().getId())
                        .toList()
        );

        Long ownerId = projectRole.getProject().getOwner().getId();

        List<User> candidates = matchingUsers.stream()
                .filter(user -> !user.getId().equals(ownerId))
                .sorted(
                        Comparator
                                .comparing(User::getAverageRating).reversed()
                                .thenComparing(Comparator.comparing(User::getCompletionRate).reversed())
                                .thenComparing(User::getId)
                )
                .toList();

        boolean enoughCandidates = candidates.size() >= projectRole.getRequiredCount();

        int limit = Math.min(projectRole.getRequiredCount() + 2, 10);

        List<User> limitedCandidates = candidates.stream()
                .limit(limit)
                .toList();

        Map<Long, Map<Long, UserSkill>> userSkillsByUser = new HashMap<>();

        for (UserSkill userSkill : userSkills) {
            userSkillsByUser
                    .computeIfAbsent(userSkill.getUser().getId(), _ -> new HashMap<>())
                    .put(userSkill.getSkill().getId(), userSkill);
        }

        List<CandidateView> candidateViews = limitedCandidates.stream()
                .map(user -> {
                    Map<Long, UserSkill> userSkillsMap = userSkillsByUser.get(user.getId());

                    List<MatchedSkillView> matchedSkill = projectRoleSkills.stream()
                            .map(pr -> {
                                UserSkill userSkill = userSkillsMap.get(pr.getSkill().getId());

                                return mapper.toMatchedSkillView(
                                        pr.getSkill().getId(),
                                        pr.getSkill().getName(),
                                        userSkill.getLevel(),
                                        pr.getMinLevel()
                                );
                            })
                            .toList();

                    return mapper.toCandidateView(
                            user.getId(),
                            user.getAverageRating(),
                            user.getCompletionRate(),
                            matchedSkill
                    );

                })
                .toList();

        return mapper.toProjectRoleCandidatesView(
                projectRoleId,
                projectRole.getRequiredCount(),
                enoughCandidates,
                candidateViews
        );
    }

    public List<ProjectRoleCandidatesView> getCandidatesForProject(
            UserPrincipal userPrincipal,
            Long projectId
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        List<ProjectRole> projectRoles = projectRoleRepository.findByProjectIdAndStatus(projectId, ProjectRoleStatus.OPEN);

        List<ProjectRoleCandidatesView> candidatesViews = new ArrayList<>();

        projectRoles.forEach(pr -> {
            try {
                candidatesViews.add(
                        getCandidates(userPrincipal, projectId, pr.getId())
                );
            } catch (ProjectRoleHasNoSkillRequirementsException _) {}
        });

        return candidatesViews;
    }
}
