package org.yvl.teamforge.project.dto.request;

import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.Instant;

@Data
public class ProjectUpdateRequest {

    private String name;

    private String description;

    @Future
    private Instant deadline;
}
