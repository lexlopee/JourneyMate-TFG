package com.example.JourneyMate.controller.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.example.JourneyMate.service.transport.IFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final IFlightService flightService;

    @GetMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam String fromId,
            @RequestParam String toId,
            @RequestParam String departDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(defaultValue = "none") String stops,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "1") Integer adults,
            @RequestParam(required = false) String childrenAge,
            @RequestParam(defaultValue = "BEST") String sort,
            @RequestParam(defaultValue = "ECONOMY") String cabinClass,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        return ResponseEntity.ok(flightService.searchFlights(
                fromId, toId, departDate, returnDate, stops,
                pageNo, adults, childrenAge, sort, cabinClass, currencyCode
        ));
    }
}