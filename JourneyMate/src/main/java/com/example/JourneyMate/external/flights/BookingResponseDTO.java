package com.example.JourneyMate.external.flights;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponseDTO {
    private boolean status;
    private String message;
    private DataDTO data;
}