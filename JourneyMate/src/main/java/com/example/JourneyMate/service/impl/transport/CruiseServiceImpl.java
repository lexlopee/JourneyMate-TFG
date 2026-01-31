package com.example.JourneyMate.service.impl.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.example.JourneyMate.service.external.transport.ICruiseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CruiseServiceImpl implements ICruiseService {

    private final RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${cruisewave.api.url}")
    private String apiUrl;

    @Value("${cruisewave.api.host}")
    private String apiHost;

    @Override
    public List<CruiseDTO> searchCruises(String startDate, String endDate, String destination,
                                         String departurePort, String currency, String country) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("start_date", startDate)
                .queryParam("end_date", endDate)
                .queryParam("destination", destination)
                .queryParam("departure_port", departurePort);

        if (currency != null) builder.queryParam("currency", currency);
        if (country != null) builder.queryParam("country", country);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<CruiseDTO> cruiseList = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.GET, entity, JsonNode.class);

            JsonNode body = response.getBody();

            if (body != null) {
                // Navegación profunda según tu log: data -> cruiseSearch -> results -> cruises
                JsonNode cruisesArray = body.path("data")
                        .path("cruiseSearch")
                        .path("results")
                        .path("cruises");

                if (cruisesArray.isArray()) {
                    for (JsonNode node : cruisesArray) {
                        cruiseList.add(mapToDTO(node));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando CruiseWave: " + e.getMessage());
        }
        return cruiseList;
    }

    private CruiseDTO mapToDTO(JsonNode node) {
        CruiseDTO dto = new CruiseDTO();

        // 1. Información del Itinerario y Barco
        JsonNode itinerary = node.path("masterSailing").path("itinerary");
        dto.setNombreCrucero(itinerary.path("name").asText("Crucero Desconocido"));
        dto.setNombreBarco(itinerary.path("ship").path("name").asText("Barco N/A"));
        dto.setNoches(itinerary.path("sailingNights").asInt(0));
        dto.setPuertoSalida(itinerary.path("departurePort").path("name").asText("N/A"));

        // 2. Información de Fecha y Precio (dentro de lowestPriceSailing)
        JsonNode lowestSailing = node.path("lowestPriceSailing");
        dto.setFechaSalida(lowestSailing.path("startDate").asText("N/A"));

        // 3. Extracción del Precio (muy profundo en la estructura)
        JsonNode priceNode = lowestSailing.path("lowestStateroomClassPrice").path("price");
        dto.setPrecioDesde(priceNode.path("value").asDouble(0.0));
        dto.setMoneda(priceNode.path("currency").path("code").asText("USD"));

        return dto;
    }
}