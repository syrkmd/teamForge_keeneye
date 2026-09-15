package org.yvl.teamforge.team.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.TeamMemberStatus;

import java.time.Instant;

@Data
public class TeamMemberView {

    private Long id;
    private TeamMemberStatus status;
    private String reason;
    private Instant joinedAt;
    private Instant leftAt;
    private Long userId;
    private Long projectRoleId;
}
