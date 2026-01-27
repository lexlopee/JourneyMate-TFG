package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.external.flights.FlightStopDTO;

import java.util.List;

public interface IBookingService {
    List<FlightStopDTO> getFlightSummary(String fromId, String toId, String departDate);
}
