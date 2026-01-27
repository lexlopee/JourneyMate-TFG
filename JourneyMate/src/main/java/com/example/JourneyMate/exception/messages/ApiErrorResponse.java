package com.example.JourneyMate.exception.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private String message;
    private String service;
    private LocalDateTime timestamp;
    private int status;

    public ApiErrorResponse(String message, String service, int status) {
        this.message = message;
        this.service = service;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }
}
