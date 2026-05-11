package com.example.JourneyMate.service.external.accommodation;

import com.example.JourneyMate.external.accommodations.HotelDTO;
import com.example.JourneyMate.external.accommodations.HotelDetailsDTO;
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
 * Servicio encargado de consumir la API externa de Booking (RapidAPI)
 * para la gestión de hoteles.
 *
 * Permite buscar hoteles, obtener detalles y resolver destinos
 * a partir de consultas de texto.
 */
@Service
public class HotelServiceExternalImpl extends BaseExternalService implements IHotelService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.api.host}")
    private String apiHost;

    private final ObjectMapper objectMapper;

    public HotelServiceExternalImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate);
        this.objectMapper = objectMapper;
    }

    /**
     * Busca hoteles en la API externa según los parámetros proporcionados.
     *
     * @param destId identificador del destino
     * @param searchType tipo de búsqueda (ej: CITY)
     * @param checkinDate fecha de entrada
     * @param checkoutDate fecha de salida
     * @param adults número de adultos
     * @param childrenAge edad de los niños (si aplica)
     * @param roomQty número de habitaciones
     * @param pageNo número de página
     * @param currencyCode código de moneda
     * @return lista de hoteles en formato {@link HotelDTO}
     */
    @Override
    public List<HotelDTO> searchHotels(String destId, String searchType, String checkinDate, String checkoutDate,
                                       Integer adults, String childrenAge, Integer roomQty,
                                       Integer pageNo, String currencyCode) {

        String url = UriComponentsBuilder.fromHttpUrl(
                        "https://booking-com15.p.rapidapi.com/api/v1/hotels/searchHotels")
                .queryParam("dest_id", destId)
                .queryParam("search_type", searchType != null ? searchType : "CITY")
                .queryParam("arrival_date", checkinDate)
                .queryParam("departure_date", checkoutDate)
                .queryParam("adults", adults != null ? adults : 1)
                .queryParam("room_qty", roomQty != null ? roomQty : 1)
                .queryParam("page_number", pageNo != null ? pageNo : 1)
                .queryParam("units", "metric")
                .queryParam("languagecode", "es")
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR")
                .toUriString();

        JsonNode body = executeGetRequest(url, apiKey, apiHost);

        List<HotelDTO> hotelList = new ArrayList<>();

        if (body != null && body.path("status").asBoolean()) {
            JsonNode hotelsArray = body.path("data").path("hotels");
            if (hotelsArray.isArray()) {
                for (JsonNode node : hotelsArray) {
                    hotelList.add(mapToDTO(node));
                }
            }
        }

        return hotelList;
    }

    /**
     * Obtiene los detalles completos de un hotel desde la API externa.
     *
     * @param hotelId identificador del hotel
     * @param arrivalDate fecha de entrada
     * @param departureDate fecha de salida
     * @param adults número de adultos
     * @param childrenAge edad de los niños
     * @param roomQty número de habitaciones
     * @param currencyCode moneda
     * @return detalles del hotel en formato {@link HotelDetailsDTO}
     */
    @Override
    public HotelDetailsDTO getHotelDetails(String hotelId, String arrivalDate, String departureDate,
                                           Integer adults, String childrenAge, Integer roomQty, String currencyCode) {

        String url = UriComponentsBuilder.fromHttpUrl(
                        "https://booking-com15.p.rapidapi.com/api/v1/hotels/getHotelDetails")
                .queryParam("hotel_id", hotelId)
                .queryParam("arrival_date", arrivalDate)
                .queryParam("departure_date", departureDate)
                .queryParam("adults", adults != null ? adults : 1)
                .queryParam("children_age", childrenAge)
                .queryParam("room_qty", roomQty != null ? roomQty : 1)
                .queryParam("units", "metric")
                .queryParam("temperature_unit", "c")
                .queryParam("currency_code", currencyCode != null ? currencyCode : "EUR")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);

        try {
            return objectMapper.treeToValue(response, HotelDetailsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al mapear los detalles del hotel", e);
        }
    }

    /**
     * Obtiene el identificador de destino (dest_id) a partir de un texto de búsqueda.
     *
     * @param query nombre de la ciudad o destino
     * @return identificador del destino o null si no se encuentra
     */
    @Override
    public String getDestinationId(String query) {

        String url = UriComponentsBuilder.fromHttpUrl(
                        "https://booking-com15.p.rapidapi.com/api/v1/hotels/searchDestination")
                .queryParam("query", query)
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);

        if (response != null && response.path("status").asBoolean()) {
            JsonNode data = response.path("data");
            if (data.isArray() && !data.isEmpty()) {
                return data.get(0).path("dest_id").asText();
            }
        }
        return null;
    }

    /**
     * Convierte un nodo JSON de la API externa en un DTO interno de hotel.
     *
     * @param node nodo JSON recibido de la API
     * @return objeto {@link HotelDTO} mapeado
     */
    private HotelDTO mapToDTO(JsonNode node) {
        HotelDTO dto = new HotelDTO();

        JsonNode prop = node.path("property");

        dto.setHotelId(prop.path("id").asText());
        dto.setNombre(prop.path("name").asText("N/A"));

        dto.setLatitud(prop.path("latitude").asDouble(0.0));
        dto.setLongitud(prop.path("longitude").asDouble(0.0));

        int stars = prop.path("accuratePropertyClass").asInt(0);
        if (stars == 0) stars = prop.path("propertyClass").asInt(0);
        dto.setPropertyClass(stars);

        JsonNode priceBreakdown = prop.path("priceBreakdown");

        JsonNode grossPrice = priceBreakdown.path("grossPrice");
        if (!grossPrice.isMissingNode()) {
            dto.setPrecio(grossPrice.path("value").asDouble(0.0));
            dto.setMoneda(grossPrice.path("currency").asText("EUR"));
        }

        JsonNode originalPriceNode = priceBreakdown.path("strikethroughPrice");
        if (!originalPriceNode.isMissingNode()) {
            dto.setPrecioOriginal(originalPriceNode.path("value").asDouble(0.0));
        } else {
            dto.setPrecioOriginal(null);
        }

        dto.setCalificacion(prop.path("reviewScore").asDouble(0.0));
        dto.setReviewWord(prop.path("reviewScoreWord").asText(""));

        JsonNode photos = prop.path("photoUrls");
        if (photos.isArray() && !photos.isEmpty()) {
            dto.setUrlFoto(photos.get(0).asText());
        }

        return dto;
    }
}