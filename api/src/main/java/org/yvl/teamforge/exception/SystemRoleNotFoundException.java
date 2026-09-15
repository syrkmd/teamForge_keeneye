package org.yvl.teamforge.exception;

import org.yvl.teamforge.entity.enums.SystemRoleName;

public class SystemRoleNotFoundException extends RuntimeException {

    public SystemRoleNotFoundException(SystemRoleName role) {
        super("System role not found: " + role);
    }
}