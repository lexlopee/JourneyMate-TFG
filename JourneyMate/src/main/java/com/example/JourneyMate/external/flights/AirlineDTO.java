package com.example.JourneyMate.external.flights;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirlineDTO {
    private String name;
    private String code;
    private String logo;
}
