package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cars.CarDTO;
import com.example.JourneyMate.external.cars.CarLocationDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl extends BaseExternalService implements ICarService {

    @Value("${rapidapi.key}")
    private String apiKey;

    // Usamos el host definitivo de la v18
    private final String API_HOST = "booking-com18.p.rapidapi.com";

    public CarServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<CarLocationDTO> searchCarLocation(String query, String languageCode, String countryFlag) {
        // Endpoint v18: /car/auto-complete
        String url = UriComponentsBuilder.fromHttpUrl("https://" + API_HOST + "/car/auto-complete")
                .queryParam("query", query)
                .queryParam("languageCode", languageCode != null ? languageCode : "en-us")
                .queryParam("countryFlag", countryFlag != null ? countryFlag : "us")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, API_HOST);
        List<CarLocationDTO> locations = new ArrayList<>();

        if (response != null && response.path("status").asBoolean()) {
            JsonNode dataArray = response.path("data");

            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    CarLocationDTO loc = new CarLocationDTO();
                    loc.setId(node.path("id").asText()); // Hash Base64 que usaremos en la búsqueda
                    loc.setName(node.path("name").asText());
                    loc.setCity(node.path("city").asText());
                    loc.setCountry(node.path("country").asText());
                    loc.setType(node.path("type").asText());
                    loc.setIataCode(node.path("iata_code").asText(null));
                    locations.add(loc);
                }
            }
        }
        return locations;
    }

    @Override
    public List<CarDTO> searchCars(String pickUpId, String dropOffId, String pDate, String pTime,
                                   String dDate, String dTime, Integer age, String currency) {

        // Endpoint síncrono v18: /car/search
        String url = UriComponentsBuilder.fromHttpUrl("https://" + API_HOST + "/car/search")
                .queryParam("pickUpId", pickUpId)
                .queryParam("pickUpDate", pDate)
                .queryParam("pickUpTime", pTime)
                .queryParam("dropOffDate", dDate)
                .queryParam("dropOffTime", dTime)
                .queryParam("dropOffId", dropOffId != null ? dropOffId : pickUpId)
                .queryParam("driverAge", age != null ? age : 30)
                .queryParam("currencyCode", currency != null ? currency : "EUR")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, API_HOST);
        List<CarDTO> finalCars = new ArrayList<>();

        if (response != null && response.has("data")) {
            // En este endpoint, la lista de coches viene en data -> search_results
            JsonNode searchResults = response.path("data").path("search_results");

            if (searchResults.isArray()) {
                for (JsonNode carNode : searchResults) {
                    CarDTO dto = new CarDTO();

                    // 1. Datos del Vehículo
                    JsonNode vehicleInfo = carNode.path("vehicle_info");
                    dto.setCarName(vehicleInfo.path("v_name").asText());
                    dto.setImageUrl(vehicleInfo.path("image_url").asText());
                    dto.setTransmission(vehicleInfo.path("transmission").asText());

                    // Manejo de asientos y cálculo total de maletas
                    dto.setSeats(vehicleInfo.path("seats").asInt(4));
                    int bigBags = vehicleInfo.path("suitcases").path("big").asInt(0);
                    int smallBags = vehicleInfo.path("suitcases").path("small").asInt(0);
                    dto.setBags(bigBags + smallBags);

                    // 2. Datos de Precio
                    JsonNode pricingInfo = carNode.path("pricing_info");
                    dto.setPrice(pricingInfo.path("price").asDouble());
                    dto.setCurrency(pricingInfo.path("currency").asText());

                    // 3. Información del Proveedor (Sixt, Payless, etc.)
                    dto.setVendorName(carNode.path("supplier_info").path("name").asText());

                    // 4. Enlace para reservar
                    dto.setDeeplink(carNode.path("forward_url").asText());

                    finalCars.add(dto);
                }
            }
        }
        return finalCars;
    }
}