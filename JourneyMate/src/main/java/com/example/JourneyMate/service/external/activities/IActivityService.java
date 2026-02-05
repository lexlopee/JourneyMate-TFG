package com.example.JourneyMate.service.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;

import java.util.List;
import java.util.Map;

public interface IActivityService {

    // Devolvemos una lista de mapas para evitar crear un DTO de localización
    List<Map<String, String>> searchLocation(String query, String languageCode);

    List<ActivityDTO> searchActivities(
            String id, // El ID de destino
            String startDate,
            String endDate,
            String sortBy,
            Integer page,
            String currencyCode,
            String languageCode,
            String typeFilters
    );
}