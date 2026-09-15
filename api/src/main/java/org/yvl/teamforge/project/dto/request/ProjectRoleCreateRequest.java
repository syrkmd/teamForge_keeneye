package org.yvl.teamforge.project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectRoleCreateRequest {

    @NotBlank
    private String roleName;

    @NotBlank
    private String description;

    @NotNull
    @Min(1)
    private Integer requiredCount;
}
