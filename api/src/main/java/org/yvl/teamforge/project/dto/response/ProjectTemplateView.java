package org.yvl.teamforge.project.dto.response;

import lombok.Data;

@Data
public class ProjectTemplateView {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}
