package com.example.JourneyMate.service.external;

import com.example.JourneyMate.external.maps.MapPredictionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MapsService extends BaseExternalService {

    @Value("${api.google.maps.key}")
    private String googleKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MapsService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Autocompletado de ciudades
     * Retorna una lista limpia de DTOs para el Frontend.
     */
    @Cacheable(value = "predictions", key = "#query")
    public List<MapPredictionDTO> getAutocomplete(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                .queryParam("input", query)
                .queryParam("types", "(cities)")
                .queryParam("language", "es")
                .queryParam("key", googleKey)
                .toUriString();

        var response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) {
            return Collections.emptyList();
        }

        // Conversión segura eliminando "Unchecked cast" warnings
        List<Map<String, Object>> predictions = objectMapper.convertValue(
                response.get("predictions"),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        return predictions.stream().map(p -> {
            Map<String, Object> structured = objectMapper.convertValue(
                    p.get("structured_formatting"),
                    new TypeReference<Map<String, Object>>() {
                    }
            );

            return MapPredictionDTO.builder()
                    .description((String) p.get("description"))
                    .placeId((String) p.get("place_id"))
                    .mainText((String) structured.get("main_text"))
                    .build();
        }).toList();
    }

    /**
     * Obtiene detalles específicos (Latitud/Longitud) para la DB (Tabla PUNTO_INTERES)
     */
    public JsonNode getPlaceDetails(String placeId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/details/json")
                .queryParam("place_id", placeId)
                .queryParam("fields", "geometry,name,formatted_address")
                .queryParam("key", googleKey)
                .toUriString();

        return executeGetRequest(url, null, null);
    }

    /**
     * Busca puntos de interés cercanos (Objetivo 9 del Anteproyecto)
     */
    public JsonNode getNearbyTouristPoints(double lat, double lon) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryParam("location", lat + "," + lon)
                .queryParam("radius", 3000)
                .queryParam("type", "tourist_attraction")
                .queryParam("key", googleKey)
                .toUriString();

        return executeGetRequest(url, null, null);
    }

    /**
     * Metodo original para obtener sugerencias crudas (JsonNode)
     */
    public JsonNode getPlaceSuggestions(String input) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                .queryParam("input", input)
                .queryParam("types", "(cities)")
                .queryParam("language", "es")
                .queryParam("key", googleKey)
                .toUriString();

        return executeGetRequest(url, null, null);
    }
}