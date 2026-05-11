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

/**
 * Servicio encargado de consumir la API externa de vuelos (Booking Flights).
 *
 * Permite buscar destinos, vuelos y obtener detalles completos de cada vuelo.
 */
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

    /**
     * Busca ubicaciones disponibles para vuelos según una consulta.
     *
     * @param query nombre de ciudad o aeropuerto
     * @return nodo JSON con los resultados de ubicaciones
     */
    @Override
    public JsonNode searchLocation(String query) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/searchDestination")
                .queryParam("query", query)
                .toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    /**
     * Busca vuelos disponibles según los parámetros de búsqueda.
     *
     * @param fromId origen (ID de aeropuerto o ciudad)
     * @param toId destino (ID de aeropuerto o ciudad)
     * @param departDate fecha de salida
     * @param returnDate fecha de regreso (opcional)
     * @param stops número de escalas (directo, 1 stop, etc.)
     * @param pageNo número de página de resultados
     * @param adults número de pasajeros adultos
     * @param childrenAge edad de los niños (no utilizado actualmente)
     * @param sort criterio de ordenación
     * @param cabinClass clase del vuelo (ECONOMY, BUSINESS, etc.)
     * @param currencyCode moneda (EUR, USD, etc.)
     * @return lista de vuelos en formato {@link FlightDTO}
     */
    @Override
    public List<FlightDTO> searchFlights(String fromId,
                                         String toId,
                                         String departDate,
                                         String returnDate,
                                         String stops,
                                         Integer pageNo,
                                         Integer adults,
                                         String childrenAge,
                                         String sort,
                                         String cabinClass,
                                         String currencyCode) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/searchFlights")
                .queryParam("fromId", fromId)
                .queryParam("toId", toId)
                .queryParam("departDate", departDate)
                .queryParam("pageNo", pageNo != null ? pageNo : 1)
                .queryParam("adults", adults != null ? adults : 1)
                .queryParam("sort", sort != null ? sort : "BEST")
                .queryParam("cabinClass", cabinClass != null ? cabinClass : "ECONOMY")
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR");

        if (stops != null && !stops.equalsIgnoreCase("none")) {
            builder.queryParam("stops", stops);
        }

        if (returnDate != null && !returnDate.isEmpty()) {
            builder.queryParam("returnDate", returnDate);
        }

        String url = builder.toUriString();

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

    /**
     * Obtiene los detalles completos de un vuelo específico.
     *
     * @param token identificador único del vuelo
     * @param currencyCode moneda
     * @return detalles del vuelo en formato {@link FlightDetailsDTO}
     */
    @Override
    public FlightDetailsDTO getFlightDetails(String token, String currencyCode) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/getFlightDetails")
                .queryParam("token", token)
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR")
                .toUriString();

        JsonNode responseNode = executeGetRequest(url, apiKey, apiHost);

        try {
            return objectMapper.treeToValue(responseNode, FlightDetailsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al mapear el detalle del vuelo", e);
        }
    }

    /**
     * Convierte un nodo JSON de vuelo en un DTO simplificado.
     *
     * @param node nodo JSON de la API
     * @return objeto {@link FlightDTO}
     */
    private FlightDTO mapToDTO(JsonNode node) {

        FlightDTO dto = new FlightDTO();

        dto.setToken(node.path("token").asText());

        JsonNode priceNode = node.path("priceBreakdown").path("total");
        dto.setPrecio(priceNode.path("units").asDouble(0.0));
        dto.setMoneda(priceNode.path("currencyCode").asText("EUR"));

        JsonNode segments = node.path("segments");

        if (segments.isArray() && segments.size() > 0) {

            JsonNode firstSegment = segments.get(0);

            int numLegs = firstSegment.path("legs").size();
            dto.setStops(numLegs - 1);

            JsonNode legs = firstSegment.path("legs");

            if (legs.isArray() && legs.size() > 0) {

                JsonNode firstLeg = legs.get(0);

                dto.setCabinClass(firstLeg.path("cabinClass").asText("ECONOMY"));

                JsonNode carriers = firstLeg.path("carriersData");
                if (carriers.isArray() && carriers.size() > 0) {
                    JsonNode carrier = carriers.get(0);
                    dto.setAerolinea(carrier.path("name").asText("N/A"));
                    dto.setLogoUrl(carrier.path("logo").asText(null));
                }

                dto.setOrigen(firstLeg.path("departureAirport").path("cityName").asText("N/A"));
                dto.setDestino(firstLeg.path("arrivalAirport").path("cityName").asText("N/A"));

                dto.setHoraSalida(firstLeg.path("departureTime").asText());
                dto.setHoraLlegada(firstLeg.path("arrivalTime").asText());

                long totalSeconds = firstSegment.path("totalTime").asLong(0);
                long hours = totalSeconds / 3600;
                long mins = (totalSeconds % 3600) / 60;

                dto.setDuracion(hours + "h " + mins + "m");
            }
        }

        return dto;
    }
}