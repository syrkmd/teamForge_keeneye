package org.yvl.teamforge.project.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.ProjectRoleStatus;

@Data
public class ProjectRoleView {

    private Long id;
    private String roleName;
    private String description;
    private Integer requiredCount;
    private ProjectRoleStatus status;
}
