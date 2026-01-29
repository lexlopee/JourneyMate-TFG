package com.example.JourneyMate.service.impl.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.example.JourneyMate.service.transport.IFlightService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements IFlightService {

    private final RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.api.url}")
    private String apiUrl;

    @Value("${booking.api.host}")
    private String apiHost;

    @Override
    public List<FlightDTO> searchFlights(String fromId, String toId, String departDate, String returnDate,
                                         String stops, Integer pageNo, Integer adults, String childrenAge,
                                         String sort, String cabinClass, String currencyCode) {

        // Construimos la URL añadiendo parámetros solo si no son nulos
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate);

        if (returnDate != null) builder.queryParam("returnDate", returnDate);
        if (stops != null && !stops.equals("none")) builder.queryParam("stops", stops);
        if (pageNo != null) builder.queryParam("pageNo", pageNo);
        if (adults != null) builder.queryParam("adults", adults);
        if (childrenAge != null) builder.queryParam("children_age", childrenAge);
        if (sort != null) builder.queryParam("sort", sort);
        if (cabinClass != null) builder.queryParam("cabinClass", cabinClass);
        if (currencyCode != null) builder.queryParam("currency_code", currencyCode);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<FlightDTO> flightList = new ArrayList<>();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, JsonNode.class);

            if (response.getBody() != null && response.getBody().has("data")) {
                JsonNode offers = response.getBody().path("data").path("flightOffers");
                if (offers.isArray()) {
                    for (JsonNode offer : offers) {
                        flightList.add(mapToDTO(offer));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en la API de Vuelos: " + e.getMessage());
        }
        return flightList;
    }

    private FlightDTO mapToDTO(JsonNode node) {
        FlightDTO dto = new FlightDTO();
        JsonNode firstSegment = node.path("segments").get(0);
        JsonNode firstLeg = firstSegment.path("legs").get(0);
        JsonNode carrier = firstLeg.path("carriersData").get(0);

        dto.setAerolinea(carrier.path("name").asText("N/A"));
        dto.setLogoUrl(carrier.path("logo").asText());
        dto.setOrigen(firstSegment.path("departureAirport").path("cityName").asText());
        dto.setDestino(firstSegment.path("arrivalAirport").path("cityName").asText());
        dto.setHoraSalida(firstSegment.path("departureTime").asText());
        dto.setHoraLlegada(firstSegment.path("arrivalTime").asText());

        JsonNode priceNode = node.path("priceBreakdown").path("total");
        dto.setPrecio(priceNode.path("units").asDouble(0.0));
        dto.setMoneda(priceNode.path("currencyCode").asText("EUR"));

        return dto;
    }
}