package com.example.JourneyMate.service.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.external.activities.ActivityDetailsDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface IActivityService {

    JsonNode searchLocation(String query);

    List<ActivityDTO> searchActivities(
            String id,
            String startDate,
            String endDate,
            String sortBy,
            Integer page,
            String currencyCode,
            String languageCode,
            String typeFilters
    );

    ActivityDetailsDTO getActivityDetails(String id, String currencyCode, String languageCode);
}