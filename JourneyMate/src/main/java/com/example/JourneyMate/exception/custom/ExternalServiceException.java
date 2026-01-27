package com.example.JourneyMate.exception.custom;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final String serviceName;

    public ExternalServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
}
