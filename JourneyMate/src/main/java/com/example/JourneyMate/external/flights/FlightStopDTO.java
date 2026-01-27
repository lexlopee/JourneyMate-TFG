package com.example.JourneyMate.external.flights;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightStopDTO {

    @JsonProperty("numberOfStops")
    private int stops;

    private int count;

    @JsonProperty("minPrice")
    private PriceDTO price;

    private AirlineDTO cheapestAirline;
}
