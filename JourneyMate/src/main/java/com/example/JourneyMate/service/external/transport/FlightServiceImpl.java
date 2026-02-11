package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
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

    @Value("${booking.api.host}")
    private String apiHost;

    public FlightServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public JsonNode searchLocation(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/searchDestination")
                .queryParam("query", query).toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    @Override
    public List<FlightDTO> searchFlights(String fromId, String toId, String departDate, String returnDate,
                                         String stops, Integer pageNo, Integer adults, String childrenAge,
                                         String sort, String cabinClass, String currencyCode) {

        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/searchFlights")
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate)
                .queryParam("returnDate", returnDate)
                .queryParam("stops", stops)
                .queryParam("pageNo", pageNo != null ? pageNo : 1)
                .queryParam("adults", adults != null ? adults : 1)
                .queryParam("childrenAge", childrenAge)
                .queryParam("sort", sort != null ? sort : "BEST")
                .queryParam("cabinClass", cabinClass != null ? cabinClass : "ECONOMY")
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);
        List<FlightDTO> flightList = new ArrayList<>();

        if (response != null && response.path("status").asBoolean()) {
            JsonNode offers = response.path("data").path("flightOffers");
            if (offers.isArray()) {
                for (JsonNode offer : offers) {
                    flightList.add(mapToDTO(offer));
                }
            }
        }
        return flightList;
    }

    private FlightDTO mapToDTO(JsonNode node) {
        FlightDTO dto = new FlightDTO();

        // 1. Precio y Moneda
        JsonNode priceNode = node.path("priceBreakdown").path("total");
        double rawPrice = priceNode.path("units").asDouble(0.0);
        dto.setPrecio(Math.round(rawPrice * 100.0) / 100.0);
        dto.setMoneda(priceNode.path("currencyCode").asText("EUR"));

        // 2. Información del trayecto
        // Accedemos al primer segmento del viaje
        JsonNode firstSegment = node.path("segments").get(0);
        JsonNode firstLeg = firstSegment.path("legs").get(0);

        // 3. Aerolínea (Tomamos la principal de la oferta)
        JsonNode carrier = firstLeg.path("carriersData").get(0);
        dto.setAerolinea(carrier.path("name").asText("N/A"));
        dto.setLogoUrl(carrier.path("logo").asText(null));

        // 4. Origen y Destino
        dto.setOrigen(firstLeg.path("departureAirport").path("cityName").asText("N/A"));
        dto.setDestino(firstLeg.path("arrivalAirport").path("cityName").asText("N/A"));

        // 5. Horas (Formato ISO de la API)
        dto.setHoraSalida(firstLeg.path("departureTime").asText());
        dto.setHoraLlegada(firstLeg.path("arrivalTime").asText());

        return dto;
    }
}