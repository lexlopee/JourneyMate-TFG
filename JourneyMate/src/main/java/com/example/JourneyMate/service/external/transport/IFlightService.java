package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface IFlightService {

    JsonNode searchLocation(String query);

    List<FlightDTO> searchFlights(
            String fromId, String toId, String departDate, String returnDate,
            String stops, Integer pageNo, Integer adults, String childrenAge,
            String sort, String cabinClass, String currencyCode
    );
}