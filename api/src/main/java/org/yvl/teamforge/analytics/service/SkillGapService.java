package org.yvl.teamforge.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.analytics.dto.projection.SkillGapRow;
import org.yvl.teamforge.analytics.dto.response.SkillGapView;
import org.yvl.teamforge.analytics.mapper.SkillGapMapper;
import org.yvl.teamforge.team.exception.TeamNotFoundException;
import org.yvl.teamforge.project.service.ProjectAccessService;
import org.yvl.teamforge.repository.ProjectRoleSkillRepository;
import org.yvl.teamforge.repository.TeamRepository;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillGapService {

    private final ProjectRoleSkillRepository projectRoleSkillRepository;
    private final TeamRepository teamRepository;
    private final SkillGapMapper skillGapMapper;
    private final ProjectAccessService projectAccessService;

    @Transactional(readOnly = true)
    public List<SkillGapView> getSkillGaps(
            UserPrincipal userPrincipal,
            Long projectId
    ) {
        projectAccessService.getProjectForModification(userPrincipal, projectId);

        teamRepository.findByProjectId(projectId).orElseThrow(() ->
                new TeamNotFoundException(projectId));

        List<SkillGapRow> skillGapRows = projectRoleSkillRepository.findSkillGapRowsByProjectId(projectId);

        return skillGapRows.stream().map(skillGapMapper::toSkillGapView).toList();
    }
}
