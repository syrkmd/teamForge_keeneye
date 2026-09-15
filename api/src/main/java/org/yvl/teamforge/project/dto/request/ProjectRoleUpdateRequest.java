package org.yvl.teamforge.project.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProjectRoleUpdateRequest {

    private String roleName;
    private String description;

    @Min(1)
    private Integer requiredCount;
}
