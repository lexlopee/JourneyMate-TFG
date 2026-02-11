package com.example.JourneyMate.service.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;

import java.util.List;
import java.util.Map;

public interface IActivityService {

    List<Map<String, String>> searchLocation(String query);

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
}