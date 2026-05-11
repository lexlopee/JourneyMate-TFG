package com.example.JourneyMate.service.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.external.activities.ActivityDetailsDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de consumir la API externa de actividades turísticas (Booking Attractions).
 *
 * Permite buscar ubicaciones, actividades y obtener detalles completos
 * de cada actividad turística.
 */
@Service
public class ActivityServiceImpl extends BaseExternalService implements IActivityService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.api.host}")
    private String apiHost;

    public ActivityServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Busca ubicaciones turísticas en la API externa según una consulta.
     *
     * @param query nombre de la ciudad o destino
     * @return nodo JSON con los resultados de ubicaciones
     */
    @Override
    public JsonNode searchLocation(String query) {

        String url = UriComponentsBuilder.fromHttpUrl(
                        "https://booking-com15.p.rapidapi.com/api/v1/attraction/searchLocation")
                .queryParam("query", "{query}")
                .queryParam("languagecode", "es")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("query", query);

        try {
            JsonNode response = executeGetWithParams(url, params);
            if (response != null && response.has("data")) {
                return response.get("data");
            }
        } catch (Exception e) {
            System.err.println("Error en searchLocation: " + e.getMessage());
        }

        return JsonNodeFactory.instance.objectNode();
    }

    /**
     * Busca actividades turísticas en la API externa.
     *
     * @param id identificador de la ubicación
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @param sortBy criterio de ordenación
     * @param page número de página
     * @param currencyCode moneda
     * @param languageCode idioma
     * @param typeFilters filtros de tipo de actividad
     * @return lista de actividades en formato {@link ActivityDTO}
     */
    @Override
    public List<ActivityDTO> searchActivities(String id, String startDate, String endDate,
                                              String sortBy, Integer page, String currencyCode,
                                              String languageCode, String typeFilters) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                        "https://booking-com15.p.rapidapi.com/api/v1/attraction/searchAttractions")
                .queryParam("id", "{id}")
                .queryParam("startDate", "{startDate}")
                .queryParam("endDate", "{endDate}")
                .queryParam("sortBy", "{sortBy}")
                .queryParam("page", "{page}")
                .queryParam("currency_code", "{currency_code}")
                .queryParam("languagecode", "es");

        if (typeFilters != null && !typeFilters.isEmpty()) {
            builder.queryParam("typeFilters", "{typeFilters}");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("sortBy", sortBy != null ? sortBy : "trending");
        params.put("page", page != null ? page : 1);
        params.put("currency_code", currencyCode != null ? currencyCode : "EUR");

        if (typeFilters != null && !typeFilters.isEmpty()) {
            params.put("typeFilters", typeFilters);
        }

        List<ActivityDTO> activityList = new ArrayList<>();

        try {
            JsonNode response = executeGetWithParams(builder.build().toUriString(), params);

            if (response != null && response.path("status").asBoolean()) {
                JsonNode products = response.path("data").path("products");
                if (products.isArray()) {
                    for (JsonNode node : products) {
                        activityList.add(mapToDTO(node));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error en searchActivities: " + e.getMessage());
        }

        return activityList;
    }

    /**
     * Obtiene los detalles completos de una actividad turística.
     *
     * @param slug identificador único de la actividad
     * @param currencyCode moneda
     * @param languageCode idioma
     * @return detalles de la actividad en formato {@link ActivityDetailsDTO}
     */
    @Override
    public ActivityDetailsDTO getActivityDetails(String slug, String currencyCode, String languageCode) {

        String url = UriComponentsBuilder.fromHttpUrl(
                        "https://booking-com15.p.rapidapi.com/api/v1/attraction/getAttractionDetails")
                .queryParam("slug", "{slug}")
                .queryParam("currency_code", "{currency_code}")
                .queryParam("languagecode", "es")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("slug", slug);
        params.put("currency_code", currencyCode != null ? currencyCode : "EUR");

        try {
            JsonNode response = executeGetWithParams(url, params);
            if (response != null && response.path("status").asBoolean()) {
                return mapToDetailDTO(response.path("data"));
            }
        } catch (Exception e) {
            System.err.println("Error en getActivityDetails: " + e.getMessage());
        }

        return null;
    }

    /**
     * Ejecuta una petición GET a la API externa con parámetros dinámicos.
     *
     * @param url URL de la petición
     * @param uriVariables variables de la URI
     * @return respuesta JSON de la API
     */
    private JsonNode executeGetWithParams(String url, Map<String, ?> uriVariables) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, JsonNode.class, uriVariables
        );

        return response.getBody();
    }

    /**
     * Convierte un nodo JSON de actividad en un DTO simplificado.
     *
     * @param node nodo JSON de la API
     * @return objeto {@link ActivityDTO}
     */
    private ActivityDTO mapToDTO(JsonNode node) {
        ActivityDTO dto = new ActivityDTO();

        dto.setIdActividad(node.path("id").asText());
        dto.setSlug(node.path("slug").asText());
        dto.setNombre(node.path("name").asText("Actividad"));
        dto.setDescripcion(node.path("shortDescription").asText("Sin descripción"));

        JsonNode priceNode = node.path("representativePrice");
        if (!priceNode.isMissingNode()) {
            double rawPrice = priceNode.path("publicAmount").asDouble(0.0);
            dto.setPrecio(Math.round(rawPrice * 100.0) / 100.0);
            dto.setMoneda(priceNode.path("currency").asText("EUR"));
        }

        dto.setUrlFoto(node.path("primaryPhoto").path("small").asText(null));

        JsonNode stats = node.path("reviewsStats").path("combinedNumericStats");
        dto.setCalificacion(stats.path("average").asDouble(0.0));

        return dto;
    }

    /**
     * Convierte un nodo JSON de detalle de actividad en DTO completo.
     *
     * @param data nodo JSON con detalles
     * @return objeto {@link ActivityDetailsDTO}
     */
    private ActivityDetailsDTO mapToDetailDTO(JsonNode data) {

        ActivityDetailsDTO dto = new ActivityDetailsDTO();

        dto.setIdActividad(data.path("id").asText());
        dto.setNombre(data.path("name").asText());
        dto.setDescripcionLarga(data.path("description").asText());
        dto.setShortDescription(data.path("shortDescription").asText());

        List<String> images = new ArrayList<>();
        data.path("photos").forEach(photo -> images.add(photo.path("medium").asText()));
        dto.setFotos(images);

        JsonNode price = data.path("representativePrice");
        dto.setPrecio(price.path("publicAmount").asDouble());
        dto.setMoneda(price.path("currency").asText("EUR"));

        List<String> inclusions = new ArrayList<>();
        data.path("whatsIncluded").forEach(node -> inclusions.add(node.asText()));
        dto.setWhatsIncluded(inclusions);

        List<String> exclusions = new ArrayList<>();
        data.path("notIncluded").forEach(node -> exclusions.add(node.asText()));
        dto.setNotIncluded(exclusions);

        dto.setHasFreeCancellation(
                data.path("cancellationPolicy").path("hasFreeCancellation").asBoolean()
        );

        JsonNode stats = data.path("reviewsStats").path("combinedNumericStats");
        dto.setAverageRating(stats.path("average").asDouble(0.0));
        dto.setTotalReviews(stats.path("total").asInt(0));

        return dto;
    }
}