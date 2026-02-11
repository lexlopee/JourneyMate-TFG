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
     * PASO 1: Buscar la ubicación para obtener el ID de destino (UFI).
     */
    @GetMapping("/location")
    public ResponseEntity<List<Map<String, String>>> getLocation(@RequestParam String query) {
        return ResponseEntity.ok(activityService.searchLocation(query));
    }

    /**
     * PASO 2: Buscar actividades usando el ID obtenido (UFI).
     */
    @GetMapping("/search")
    public ResponseEntity<List<ActivityDTO>> searchActivities(
            @RequestParam String id,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "trending") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        return ResponseEntity.ok(activityService.searchActivities(
                id, startDate, endDate, sortBy, page, currencyCode, "es", null
        ));
    }
}