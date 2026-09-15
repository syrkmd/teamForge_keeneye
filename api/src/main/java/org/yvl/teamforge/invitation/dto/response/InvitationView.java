package org.yvl.teamforge.invitation.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.InvitationStatus;

import java.time.Instant;

@Data
public class InvitationView {

    private Long id;
    private InvitationStatus status;
    private Instant createdAt;
    private Instant respondedAt;
    private Long projectId;
    private Long projectRoleId;
    private Long userId;
}
