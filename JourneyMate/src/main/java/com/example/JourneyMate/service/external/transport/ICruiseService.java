package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ICruiseService {

    JsonNode getDestinations();

    JsonNode getPorts();

    List<CruiseDTO> searchCruises(
            String startDate, String endDate, String destination,
            String departurePort, String currency, String country
    );
}