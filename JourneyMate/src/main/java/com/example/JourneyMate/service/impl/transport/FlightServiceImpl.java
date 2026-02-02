package com.example.JourneyMate.service.impl.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.example.JourneyMate.service.external.transport.IFlightService;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightServiceImpl extends BaseExternalService implements IFlightService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.flights.url}")
    private String apiUrl;

    @Value("${booking.api.host}")
    private String apiHost;

    // El constructor pasa el restTemplate a la clase base
    public FlightServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<FlightDTO> searchFlights(String fromId, String toId, String departDate, String returnDate,
                                         String stops, Integer pageNo, Integer adults, String childrenAge,
                                         String sort, String cabinClass, String currencyCode) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate);

        // ... (tus validaciones de parámetros nulos se quedan igual)
        if (returnDate != null) builder.queryParam("returnDate", returnDate);
        if (currencyCode != null) builder.queryParam("currency_code", currencyCode);

        // USAMOS EL MÉTODO HEREDADO (Aquí se elimina la duplicidad)
        JsonNode body = executeGetRequest(builder.toUriString(), apiKey, apiHost);

        List<FlightDTO> flightList = new ArrayList<>();

        if (body != null && body.has("data")) {
            JsonNode offers = body.path("data").path("flightOffers");
            if (offers.isArray()) {
                for (JsonNode offer : offers) {
                    flightList.add(mapToDTO(offer));
                }
            }
        }
        return flightList;
    }

    private FlightDTO mapToDTO(JsonNode node) {
        // ... (tu lógica de mapeo se queda exactamente igual)
        FlightDTO dto = new FlightDTO();
        // seters...
        return dto;
    }
}