package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.example.JourneyMate.service.external.transport.ICruiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cruises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalCruiseController {

    private final ICruiseService cruiseService;

    @GetMapping("/search")
    public ResponseEntity<List<CruiseDTO>> searchCruises(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String destination,
            @RequestParam String departurePort,
            @RequestParam(defaultValue = "EUR") String currency,
            @RequestParam(defaultValue = "ESP") String country) {

        return ResponseEntity.ok(cruiseService.searchCruises(
                startDate, endDate, destination, departurePort, currency, country
        ));
    }
}