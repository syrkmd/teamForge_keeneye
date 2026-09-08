package org.yvl.teamforge.team.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.TeamStatus;

import java.time.Instant;

@Data
public class TeamView {

    private Long id;
    private TeamStatus status;
    private Instant createdAt;
    private Long projectId;
}
