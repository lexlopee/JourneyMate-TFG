package com.example.JourneyMate.service.impl.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.example.JourneyMate.service.external.activities.IActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl extends BaseExternalService implements IActivityService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.api.host}")
    private String apiHost;

    @Value("${booking.activities.url}")
    private String activitiesUrl;

    public ActivityServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<Map<String, String>> searchLocation(String query, String languageCode) {
        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/attraction/searchLocation")
                .queryParam("query", query)
                .queryParam("languagecode", languageCode != null ? languageCode : "es")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);
        List<Map<String, String>> locations = new ArrayList<>();

        if (response != null && response.path("status").asBoolean()) {
            JsonNode destinations = response.path("data").path("destinations");
            if (destinations.isArray()) {
                for (JsonNode node : destinations) {
                    Map<String, String> location = new HashMap<>();
                    location.put("id", node.path("id").asText());
                    location.put("nombre", node.path("cityName").asText());
                    location.put("descripcion", node.path("display_name").asText()); // Nombre completo (Ciudad, País)
                    locations.add(location);
                }
            }
        }
        return locations;
    }

    @Override
    public List<ActivityDTO> searchActivities(String id, String startDate, String endDate,
                                              String sortBy, Integer page, String currencyCode,
                                              String languageCode, String typeFilters) {

        // Construimos la URL
        String url = UriComponentsBuilder.fromHttpUrl(activitiesUrl)
                .queryParam("id", id)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .queryParam("sortBy", sortBy != null ? sortBy : "trending")
                .queryParam("page", page != null ? page : 1)
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR")
                .queryParam("languagecode", languageCode != null ? languageCode : "es")
                .queryParam("typeFilters", typeFilters)
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);
        List<ActivityDTO> activityList = new ArrayList<>();

        if (response != null && response.path("status").asBoolean()) {
            JsonNode products = response.path("data").path("products");
            if (products.isArray()) {
                for (JsonNode node : products) {
                    activityList.add(mapToDTO(node));
                }
            }
        }
        return activityList;
    }

    private ActivityDTO mapToDTO(JsonNode node) {
        ActivityDTO dto = new ActivityDTO();

        dto.setIdActividad(node.path("id").asText());
        dto.setNombre(node.path("name").asText("Actividad"));
        dto.setDescripcion(node.path("shortDescription").asText("Sin descripción"));

        // Precio (según tu JSON anterior)
        JsonNode priceNode = node.path("representativePrice");
        double rawPrice = priceNode.path("publicAmount").asDouble(0.0);
        dto.setPrecio(Math.round(rawPrice * 100.0) / 100.0);
        dto.setMoneda(priceNode.path("currency").asText("EUR"));

        // Foto y Calificación
        dto.setUrlFoto(node.path("primaryPhoto").path("small").asText(null));
        JsonNode stats = node.path("reviewsStats").path("combinedNumericStats");
        dto.setCalificacion(stats.path("average").asDouble(0.0));

        return dto;
    }
}