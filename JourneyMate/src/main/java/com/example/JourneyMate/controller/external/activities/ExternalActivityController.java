package com.example.JourneyMate.controller.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.service.external.activities.IActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/activities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExternalActivityController {

    private final IActivityService activityService;

    /**
     * PASO 1: Buscar la ubicación para obtener el ID de destino.
     * Ejemplo: GET /api/v1/activities/location?query=Madrid
     */
    @GetMapping("/location")
    public ResponseEntity<List<Map<String, String>>> getLocation(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "es") String languageCode) {

        List<Map<String, String>> locations = activityService.searchLocation(query, languageCode);
        return ResponseEntity.ok(locations);
    }

    /**
     * PASO 2: Buscar actividades usando el ID obtenido en el paso anterior.
     * Ejemplo: GET /api/v1/activities/search?id=eyJ1ZmkiOi0zOTA2MjV9
     */
    @GetMapping("/search")
    public ResponseEntity<List<ActivityDTO>> searchActivities(
            @RequestParam String id,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "trending") String sortBy,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "EUR") String currencyCode,
            @RequestParam(required = false, defaultValue = "es") String languageCode,
            @RequestParam(required = false) String typeFilters) {

        List<ActivityDTO> activities = activityService.searchActivities(
                id, startDate, endDate, sortBy, page, currencyCode, languageCode, typeFilters
        );
        return ResponseEntity.ok(activities);
    }
}
