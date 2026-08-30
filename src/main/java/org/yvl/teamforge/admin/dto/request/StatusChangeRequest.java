package org.yvl.teamforge.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusChangeRequest {

    @NotNull
    private Boolean active;
}
