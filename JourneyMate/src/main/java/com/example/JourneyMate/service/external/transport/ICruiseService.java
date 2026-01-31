package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;

import java.util.List;

public interface ICruiseService {
    List<CruiseDTO> searchCruises(
            String startDate, String endDate, String destination,
            String departurePort, String currency, String country
    );
}