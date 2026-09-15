package org.yvl.teamforge.profile.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @Size(min = 2)
    private String firstName;

    @Size(min = 2)
    private String lastName;

    private String about;

    private String githubUsername;
}
