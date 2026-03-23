package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.example.JourneyMate.external.flights.FlightDetailsDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    public FlightServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate);
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode searchLocation(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/searchDestination")
                .queryParam("query", query)
                .toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    @Override
    public List<FlightDTO> searchFlights(String fromId, String toId, String departDate, String returnDate,
                                         String stops, Integer pageNo, Integer adults, String childrenAge,
                                         String sort, String cabinClass, String currencyCode) {

        // Usamos el Builder para añadir parámetros condicionalmente
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/searchFlights")
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate)
                .queryParam("pageNo", pageNo != null ? pageNo : 1)
                .queryParam("adults", adults != null ? adults : 1)
                .queryParam("sort", sort != null ? sort : "BEST")
                .queryParam("cabinClass", cabinClass != null ? cabinClass : "ECONOMY")
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR");

        // Solo añadimos stops si no es "none"
        if (stops != null && !stops.equalsIgnoreCase("none")) {
            builder.queryParam("stops", stops);
        }

        // Solo añadimos fecha de regreso si existe (vuelos ida y vuelta)
        if (returnDate != null && !returnDate.isEmpty()) {
            builder.queryParam("returnDate", returnDate);
        }

        // --- ELIMINADO: No procesamos childrenAge para evitar errores de la API ---

        String url = builder.toUriString();
        JsonNode response = executeGetRequest(url, apiKey, apiHost);
        List<FlightDTO> flightList = new ArrayList<>();

        // Verificamos "status" true y que existan datos
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

    @Override
    public FlightDetailsDTO getFlightDetails(String token, String currencyCode) {
        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/getFlightDetails")
                .queryParam("token", token)
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR")
                .toUriString();

        JsonNode responseNode = executeGetRequest(url, apiKey, apiHost);

        try {
            // treeToValue mapea el nodo JSON directamente a tu clase FlightDetailsDTO
            return objectMapper.treeToValue(responseNode, FlightDetailsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al mapear el detalle del vuelo", e);
        }
    }

    private FlightDTO mapToDTO(JsonNode node) {
        FlightDTO dto = new FlightDTO();

        // 1. Token y Precio
        dto.setToken(node.path("token").asText());
        JsonNode priceNode = node.path("priceBreakdown").path("total");
        dto.setPrecio(priceNode.path("units").asDouble(0.0));
        dto.setMoneda(priceNode.path("currencyCode").asText("EUR"));

        // 2. Procesar Segmentos
        JsonNode segments = node.path("segments");
        if (segments.isArray() && segments.size() > 0) {
            // Usamos el primer segmento para la info general de la tarjeta
            JsonNode firstSegment = segments.get(0);

            // --- AQUÍ ESTÁ LA CORRECCIÓN DE LAS PARADAS ---
            // Si hay más de un leg, es porque hay escalas
            int numLegs = firstSegment.path("legs").size();
            dto.setStops(numLegs - 1);

            JsonNode legs = firstSegment.path("legs");
            if (legs.isArray() && legs.size() > 0) {
                JsonNode firstLeg = legs.get(0);

                // --- AQUÍ ESTÁ LA CORRECCIÓN DE LA CLASE ---
                // Extraemos la cabinClass real del primer leg (ECONOMY, BUSINESS, etc.)
                dto.setCabinClass(firstLeg.path("cabinClass").asText("ECONOMY"));

                // Aerolínea y Logo
                JsonNode carriers = firstLeg.path("carriersData");
                if (carriers.isArray() && carriers.size() > 0) {
                    JsonNode carrier = carriers.get(0);
                    dto.setAerolinea(carrier.path("name").asText("N/A"));
                    dto.setLogoUrl(carrier.path("logo").asText(null));
                }

                // Origen y Destino
                dto.setOrigen(firstLeg.path("departureAirport").path("cityName").asText("N/A"));
                dto.setDestino(firstLeg.path("arrivalAirport").path("cityName").asText("N/A"));

                // Horas
                dto.setHoraSalida(firstLeg.path("departureTime").asText());
                dto.setHoraLlegada(firstLeg.path("arrivalTime").asText());

                // Duración (opcional, convirtiendo segundos a formato legible si quieres)
                long totalSeconds = firstSegment.path("totalTime").asLong(0);
                long hours = totalSeconds / 3600;
                long mins = (totalSeconds % 3600) / 60;
                dto.setDuracion(hours + "h " + mins + "m");
            }
        }
        return dto;
    }
}