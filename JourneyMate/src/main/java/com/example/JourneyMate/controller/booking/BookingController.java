package com.example.JourneyMate.controller.booking;

import com.example.JourneyMate.external.flights.FlightStopDTO;
import com.example.JourneyMate.service.booking.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService bookingService;

    // Endpoint: http://localhost:8080/api/v1/flights/summary
    @GetMapping("/summary")
    public List<FlightStopDTO> getFlights(
            @RequestParam String fromId,
            @RequestParam String toId,
            @RequestParam String departDate) {

        return bookingService.getFlightSummary(fromId, toId, departDate);
    }
}