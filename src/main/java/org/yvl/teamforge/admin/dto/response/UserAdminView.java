package org.yvl.teamforge.admin.dto.response;

import lombok.Data;
import org.yvl.teamforge.entity.enums.SystemRoleName;

import java.time.Instant;

@Data
public class UserAdminView {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String about;
    private String githubUsername;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private Double averageRating;
    private Integer reviewsCount;
    private Integer completedProjectsCount;
    private Double completionRate;
    private SystemRoleName role;
}
