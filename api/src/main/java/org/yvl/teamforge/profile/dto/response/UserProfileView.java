package org.yvl.teamforge.profile.dto.response;

import lombok.Data;

@Data
public class UserProfileView {

    private String firstName;
    private String lastName;
    private String about;
    private String githubUsername;
}
