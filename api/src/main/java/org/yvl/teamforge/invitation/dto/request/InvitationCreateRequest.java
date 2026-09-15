package org.yvl.teamforge.invitation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvitationCreateRequest {

    @NotNull
    private Long userId;
}
