package org.yvl.notificationservice.exception.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    private final String message;

    private final LocalDateTime timestamp;

    public ApiError(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}