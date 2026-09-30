package org.yvl.authenticationservice.exception;

import org.yvl.authenticationservice.entity.enums.SystemRoleName;

public class SystemRoleNotFoundException extends RuntimeException {

    public SystemRoleNotFoundException(SystemRoleName role) {
        super("System role not found: " + role);
    }
}
