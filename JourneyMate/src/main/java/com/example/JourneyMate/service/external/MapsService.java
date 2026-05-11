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

/**
 * Servicio encargado de la integración con Google Maps Platform (Places API).
 *
 * Proporciona funcionalidades de autocompletado de ciudades, obtención de detalles
 * de lugares y búsqueda de puntos turísticos cercanos.
 */
@Service
public class MapsService extends BaseExternalService {

    @Value("${api.google.maps.key}")
    private String googleKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MapsService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Obtiene sugerencias de autocompletado de ciudades usando Google Places API.
     *
     * Los resultados se cachean para mejorar rendimiento y reducir llamadas a la API.
     *
     * @param query texto introducido por el usuario
     * @return lista de predicciones de lugares
     */
    @Cacheable(value = "predictions", key = "#query")
    public List<MapPredictionDTO> getAutocomplete(String query) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                .queryParam("input", query)
                .queryParam("types", "(cities)")
                .queryParam("language", "es")
                .queryParam("key", googleKey)
                .toUriString();

        var response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> predictions = objectMapper.convertValue(
                response.get("predictions"),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        return predictions.stream().map(p -> {

            Map<String, Object> structured = objectMapper.convertValue(
                    p.get("structured_formatting"),
                    new TypeReference<Map<String, Object>>() {}
            );

            return MapPredictionDTO.builder()
                    .description((String) p.get("description"))
                    .placeId((String) p.get("place_id"))
                    .mainText((String) structured.get("main_text"))
                    .build();
        }).toList();
    }

    /**
     * Obtiene información detallada de un lugar a partir de su placeId.
     *
     * @param placeId identificador único de Google Places
     * @return nodo JSON con detalles del lugar
     */
    public JsonNode getPlaceDetails(String placeId) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/place/details/json")
                .queryParam("place_id", placeId)
                .queryParam("fields", "geometry,name,formatted_address")
                .queryParam("key", googleKey)
                .toUriString();

        return restTemplate.getForObject(url, JsonNode.class);
    }

    /**
     * Obtiene puntos turísticos cercanos a una ubicación geográfica.
     *
     * @param lat latitud del punto de referencia
     * @param lon longitud del punto de referencia
     * @return nodo JSON con atracciones turísticas cercanas
     */
    public JsonNode getNearbyTouristPoints(double lat, double lon) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryParam("location", lat + "," + lon)
                .queryParam("radius", 3000)
                .queryParam("type", "tourist_attraction")
                .queryParam("key", googleKey)
                .toUriString();

        return restTemplate.getForObject(url, JsonNode.class);
    }

    /**
     * Obtiene sugerencias de lugares basadas en texto de entrada.
     *
     * Similar a {@link #getAutocomplete(String)} pero devuelve la respuesta cruda de la API.
     *
     * @param input texto de búsqueda del usuario
     * @return nodo JSON con sugerencias de Google Places
     */
    public JsonNode getPlaceSuggestions(String input) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json")
                .queryParam("input", input)
                .queryParam("types", "(cities)")
                .queryParam("language", "es")
                .queryParam("key", googleKey)
                .toUriString();

        return restTemplate.getForObject(url, JsonNode.class);
    }
}