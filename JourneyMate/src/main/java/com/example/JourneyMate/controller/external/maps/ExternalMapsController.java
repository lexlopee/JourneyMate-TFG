package com.example.JourneyMate.controller.external.maps;

import com.example.JourneyMate.service.external.MapsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalMapsController {

    private final MapsService mapsService;

    @GetMapping("/autocomplete")
    public ResponseEntity<JsonNode> autocomplete(@RequestParam String query) {
        return ResponseEntity.ok(mapsService.getPlaceSuggestions(query));
    }

    @GetMapping("/nearby")
    public ResponseEntity<JsonNode> getNearby(
            @RequestParam double lat,
            @RequestParam double lon) {

        return ResponseEntity.ok(mapsService.getNearbyTouristPoints(lat, lon));
    }
}