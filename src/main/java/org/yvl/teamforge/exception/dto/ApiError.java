package org.yvl.teamforge.exception.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    private final String message;

    private final StatusCode code;

    private final LocalDateTime timestamp;

    public ApiError(String message, StatusCode code) {
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
}
