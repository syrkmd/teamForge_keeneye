package org.yvl.teamforge.exception.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatusCode {
    UNAUTHORIZED,
    FORBIDDEN,
    BAD_REQUEST,
    EMAIL_ALREADY_EXISTS,
    INVALID_REFRESH_TOKEN,
    VALIDATION_ERROR,
    INVALID_AUTH_PRINCIPAL,
    INVALID_CREDENTIALS,
    USER_BLOCKED,
    INTERNAL_SERVER_ERROR
}
