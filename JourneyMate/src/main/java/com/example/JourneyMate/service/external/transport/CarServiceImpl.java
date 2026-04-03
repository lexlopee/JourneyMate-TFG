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

    private final String API_HOST = "booking-com18.p.rapidapi.com";

    public CarServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<CarLocationDTO> searchCarLocation(String query, String languageCode, String countryFlag) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://" + API_HOST + "/car/auto-complete")
                .queryParam("query", query)
                .queryParam("languageCode", languageCode != null ? languageCode : "es")
                .queryParam("countryFlag", countryFlag != null ? countryFlag : "es")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, API_HOST);
        List<CarLocationDTO> locations = new ArrayList<>();

        if (response != null && response.path("data").isArray()) {
            for (JsonNode node : response.path("data")) {
                CarLocationDTO loc = new CarLocationDTO();
                loc.setId(node.path("id").asText());
                loc.setName(node.path("name").asText());
                loc.setCity(node.path("city").asText());
                loc.setCountry(node.path("country").asText());
                loc.setType(node.path("type").asText());
                loc.setIataCode(node.path("iata_code").asText(null));
                locations.add(loc);
            }
        }
        return locations;
    }

    @Override
    public List<CarDTO> searchCars(String pickUpId, String dropOffId, String pDate, String pTime,
                                   String dDate, String dTime, Integer age, String currency, String carType) {

        // ✅ FIX: El pickUpId es un token base64 que ya contiene '=' codificado como %3D.
        // UriComponentsBuilder.queryParam() lo re-escapa a %253D (doble encoding), rompiendo la petición.
        // Solución: construir la query string manualmente para estos parámetros y usar
        // fromUri() con encode=false para que Spring no toque los valores ya codificados.

        String effectiveDropOff = (dropOffId != null && !dropOffId.isEmpty()) ? dropOffId : pickUpId;

        StringBuilder query = new StringBuilder();
        query.append("pickUpId=").append(pickUpId);
        query.append("&dropOffId=").append(effectiveDropOff);
        query.append("&pickUpDate=").append(pDate);
        query.append("&pickUpTime=").append(pTime);
        query.append("&dropOffDate=").append(dDate);
        query.append("&dropOffTime=").append(dTime);
        query.append("&driverAge=").append(age != null ? age : 30);
        query.append("&currencyCode=").append(currency != null ? currency : "EUR");
        query.append("&units=metric");

        if (carType != null && !carType.isEmpty()) {
            query.append("&carType=").append(carType);
        }

        // URI.create() no re-escapa nada — el string llega tal cual a la API
        String url = "https://" + API_HOST + "/car/search?" + query;

        JsonNode response = executeGetRequest(url, apiKey, API_HOST);
        List<CarDTO> carList = new ArrayList<>();

        if (response != null && response.has("data")) {
            JsonNode searchResults = response.path("data").path("search_results");

            if (searchResults.isArray()) {
                for (JsonNode carNode : searchResults) {
                    CarDTO dto = new CarDTO();
                    JsonNode vInfo = carNode.path("vehicle_info");
                    dto.setCarName(vInfo.path("v_name").asText());
                    dto.setImageUrl(vInfo.path("image_url").asText());
                    dto.setTransmission(vInfo.path("transmission").asText());
                    dto.setSeats(vInfo.path("seats").asInt());

                    int big = vInfo.path("suitcases").path("big").asInt(0);
                    int small = vInfo.path("suitcases").path("small").asInt(0);
                    dto.setBags(big + small);

                    JsonNode pInfo = carNode.path("pricing_info");
                    dto.setPrice(pInfo.path("drive_away_price").asDouble());
                    dto.setCurrency(pInfo.path("currency").asText());

                    dto.setVendorName(carNode.path("supplier_info").path("name").asText());
                    dto.setDeeplink(carNode.path("forward_url").asText());

                    carList.add(dto);
                }
            }
        }
        return carList;
    }
}