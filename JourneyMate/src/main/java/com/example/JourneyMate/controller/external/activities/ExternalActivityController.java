package com.example.JourneyMate.controller.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.external.activities.ActivityDetailsDTO;
import com.example.JourneyMate.service.external.activities.IActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExternalActivityController {

    private final IActivityService activityService;

    /**
     * PASO 1: Buscar la ubicación para obtener el ID de destino (UFI).
     */
    @GetMapping("/location")
    public ResponseEntity<JsonNode> getLocation(@RequestParam String query) {
        return ResponseEntity.ok(activityService.searchLocation(query));
    }

    /**
     * PASO 2: Buscar actividades usando el ID obtenido (UFI).
     */
    @GetMapping("/search")
    public ResponseEntity<List<ActivityDTO>> searchActivities(
            @RequestParam String id,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "trending") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        List<ActivityDTO> result = activityService.searchActivities(
                id, startDate, endDate, sortBy, page, currencyCode, "es", null
        );

        System.out.println("Resultados encontrados: " + (result != null ? result.size() : "null"));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/details")
    public ResponseEntity<ActivityDetailsDTO> getActivityDetails(
            @RequestParam String slug,
            @RequestParam(defaultValue = "EUR") String currencyCode) {
        return ResponseEntity.ok(activityService.getActivityDetails(slug, currencyCode, "es"));
    }
}