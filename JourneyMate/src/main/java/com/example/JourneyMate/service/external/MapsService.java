package com.example.JourneyMate.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MapsService extends BaseExternalService {

    @Value("${api.google.maps.key}")
    private String googleKey;

    public MapsService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public JsonNode getPlaceSuggestions(String input) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                .queryParam("input", input)
                .queryParam("types", "(cities)")
                .queryParam("language", "es")
                .queryParam("key", googleKey)
                .toUriString();

        return executeGetRequest(url, null, null);
    }


    public JsonNode getNearbyTouristPoints(double lat, double lon) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryParam("location", lat + "," + lon)
                .queryParam("radius", 3000)
                .queryParam("type", "tourist_attraction")
                .queryParam("key", googleKey)
                .toUriString();

        return executeGetRequest(url, null, null);
    }
}