package com.example.JourneyMate.service.impl.transport;

import com.example.JourneyMate.external.cars.CarDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.example.JourneyMate.service.external.transport.ICarService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarServiceImpl extends BaseExternalService implements ICarService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${booking.api.host}")
    private String apiHost;

    @Value("${booking.cars.url.location}")
    private String locationUrl;

    @Value("${booking.cars.url.search}")
    private String searchUrl;

    public CarServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<Map<String, Object>> searchCarLocation(String query, String languagecode) {
        String url = UriComponentsBuilder.fromHttpUrl(locationUrl)
                .queryParam("query", query)
                .queryParam("languagecode", languagecode != null ? languagecode : "es")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);
        List<Map<String, Object>> locations = new ArrayList<>();

        if (response != null && response.path("status").asBoolean()) {
            JsonNode dataNode = response.path("data");
            if (dataNode.isArray()) {
                for (JsonNode node : dataNode) {
                    Map<String, Object> loc = new HashMap<>();
                    // Mapeo directo del JSON de Booking
                    loc.put("label", node.path("name").asText(node.path("city").asText("Ubicación")));

                    // Acceso correcto al objeto anidado 'coordinates'
                    JsonNode coords = node.path("coordinates");
                    loc.put("lat", coords.path("latitude").asDouble());
                    loc.put("lon", coords.path("longitude").asDouble());

                    locations.add(loc);
                }
            }
        }
        return locations;
    }

    @Override
    public List<CarDTO> searchCars(Double pLat, Double pLon, Double dLat, Double dLon,
                                   String pDate, String pTime, String dDate, String dTime,
                                   Integer age, String currency) {

        String url = UriComponentsBuilder.fromHttpUrl(searchUrl)
                .queryParam("pick_up_latitude", pLat)
                .queryParam("pick_up_longitude", pLon)
                .queryParam("drop_off_latitude", dLat)
                .queryParam("drop_off_longitude", dLon)
                .queryParam("pick_up_date", pDate)
                .queryParam("drop_off_date", dDate)
                .queryParam("pick_up_time", pTime)
                .queryParam("drop_off_time", dTime)
                .queryParam("driver_age", age != null ? age : 30)
                .queryParam("currency_code", currency != null ? currency : "EUR")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, apiHost);
        List<CarDTO> carList = new ArrayList<>();

        if (response != null && response.path("status").asBoolean()) {
            JsonNode results = response.path("data").path("search_results");
            if (results.isArray()) {
                for (JsonNode node : results) {
                    carList.add(mapToDTO(node, currency != null ? currency : "EUR"));
                }
            }
        }
        return carList;
    }

    private CarDTO mapToDTO(JsonNode node, String currency) {
        CarDTO dto = new CarDTO();
        JsonNode vInfo = node.path("vehicle_info");

        dto.setId(node.path("id").asText(null));
        dto.setNombreVehiculo(vInfo.path("v_name").asText("Modelo no disponible"));
        dto.setTipo(vInfo.path("v_group").asText("Estándar"));
        dto.setPlazas(vInfo.path("seats").asInt(5));
        dto.setTransmision(vInfo.path("transmission").asText("N/A"));

        dto.setProveedor(node.path("supplier_info").path("name").asText("Proveedor"));
        dto.setUrlFoto(vInfo.path("image_url").asText());

        // Procesamiento del precio
        double price = node.path("pricing_info").path("price").asDouble(0.0);
        dto.setPrecioTotal(Math.round(price * 100.0) / 100.0);
        dto.setMoneda(currency);

        return dto;
    }
}