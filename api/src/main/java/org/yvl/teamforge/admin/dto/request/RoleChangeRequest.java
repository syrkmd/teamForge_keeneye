package org.yvl.teamforge.admin.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.yvl.teamforge.entity.enums.SystemRoleName;

@Data
public class RoleChangeRequest {

    @NotNull
    private SystemRoleName role;
}
