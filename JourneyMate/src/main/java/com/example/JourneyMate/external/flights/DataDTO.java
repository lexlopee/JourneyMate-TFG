package com.example.JourneyMate.external.flights;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataDTO {
    private AggregationDTO aggregation; // Entra al segundo nivel
}