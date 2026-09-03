package org.yvl.teamforge.project.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ProjectCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Future
    private Instant deadline;

    @NotNull
    private Long templateId;
}
