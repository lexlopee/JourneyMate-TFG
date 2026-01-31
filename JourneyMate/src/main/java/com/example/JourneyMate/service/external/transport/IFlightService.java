package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.flights.FlightDTO;

import java.util.List;

public interface IFlightService {
    List<FlightDTO> searchFlights(
            String fromId, String toId, String departDate, String returnDate,
            String stops, Integer pageNo, Integer adults, String childrenAge,
            String sort, String cabinClass, String currencyCode
    );
}