package com.example.JourneyMate.service.external.accommodation;

import com.example.JourneyMate.external.accommodations.HotelDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelsServiceImpl extends BaseExternalService implements IHotelService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.api.host}")
    private String apiHost;

    public HotelsServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<HotelDTO> searchHotels(String destId, String searchType, String checkinDate, String checkoutDate,
                                       Integer adults, String childrenAge, Integer roomQty,
                                       Integer pageNo, String currencyCode) {

        // Construcción de la URL con los parámetros específicos de la doc de hoteles
        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/hotels/searchHotels")
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

        // Ejecutamos la petición usando el metodo de la clase Base
        JsonNode body = executeGetRequest(url, apiKey, apiHost);

        List<HotelDTO> hotelList = new ArrayList<>();

        if (body != null && body.path("status").asBoolean()) {
            JsonNode hotelsArray = body.path("data").path("hotels");
            if (hotelsArray.isArray() && !hotelsArray.isEmpty()) {
                for (JsonNode node : hotelsArray) {
                    hotelList.add(mapToDTO(node));
                }
            }
        }
        return hotelList;
    }

    @Override
    public String getDestinationId(String query) {
        // Construimos la URL para buscar el destino
        String url = UriComponentsBuilder.fromHttpUrl("https://booking-com15.p.rapidapi.com/api/v1/hotels/searchDestination")
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

    private HotelDTO mapToDTO(JsonNode node) {
        HotelDTO dto = new HotelDTO();
        // La API a veces devuelve los datos dentro de "property" y otras veces directos
        JsonNode prop = node.has("property") ? node.path("property") : node;

        dto.setNombre(prop.path("name").asText("N/A"));
        dto.setLatitud(prop.path("latitude").asDouble(0.0));
        dto.setLongitud(prop.path("longitude").asDouble(0.0));

        // Mapeo de precios (Ajustado para ser más flexible)
        JsonNode priceBreakdown = prop.path("priceBreakdown");
        JsonNode grossPrice = priceBreakdown.path("grossPrice");

        if (grossPrice.isMissingNode()) {
            // Intento alternativo si la estructura es diferente
            dto.setPrecio(prop.path("price").asDouble(0.0));
            dto.setMoneda(prop.path("currency").asText("EUR"));
        } else {
            double rawPrice = grossPrice.path("value").asDouble(0.0);
            dto.setPrecio(Math.round(rawPrice * 100.0) / 100.0);
            dto.setMoneda(grossPrice.path("currency").asText("EUR"));
        }

        dto.setCalificacion(prop.path("reviewScore").asDouble(0.0));
        dto.setReviewWord(prop.path("reviewScoreWord").asText("Sin calificar"));

        // Foto: Algunas APIs usan 'photoUrls', otras 'image_url'
        if (prop.path("photoUrls").isArray() && !prop.path("photoUrls").isEmpty()) {
            dto.setUrlFoto(prop.path("photoUrls").get(0).asText());
        } else {
            dto.setUrlFoto(prop.path("wishlist_canvas_image_url").asText(null));
        }

        return dto;
    }
}