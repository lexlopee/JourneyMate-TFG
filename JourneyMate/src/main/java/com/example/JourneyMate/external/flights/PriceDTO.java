package com.example.JourneyMate.external.flights;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceDTO {
    private String currencyCode;
    private Long units;
    private Integer nanos;
}
