package org.yvl.teamforge.project.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.ProjectStatus;

import java.time.Instant;

@Data
public class ProjectView {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Instant deadline;
    private Instant createdAt;
    private Instant updatedAt;
    private Long templateId;
    private Long ownerId;
}
