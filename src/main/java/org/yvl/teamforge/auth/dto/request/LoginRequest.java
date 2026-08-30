package org.yvl.teamforge.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(min = 8)
    String password;
}
