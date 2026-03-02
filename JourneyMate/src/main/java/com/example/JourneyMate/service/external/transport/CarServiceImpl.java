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
        String url = UriComponentsBuilder.fromHttpUrl("https://" + API_HOST + "/car/auto-complete")
                .queryParam("query", query)
                .queryParam("languageCode", languageCode != null ? languageCode : "en-us")
                .queryParam("countryFlag", countryFlag != null ? countryFlag : "us")
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
                                   String dDate, String dTime, Integer age, String currency) {

        String url = UriComponentsBuilder.fromHttpUrl("https://" + API_HOST + "/car/search")
                .queryParam("pickUpId", pickUpId)
                .queryParam("pickUpDate", pDate)
                .queryParam("pickUpTime", pTime)
                .queryParam("dropOffDate", dDate)
                .queryParam("dropOffTime", dTime)
                .queryParam("dropOffId", dropOffId != null ? dropOffId : pickUpId)
                .queryParam("driverAge", age != null ? age : 30)
                .queryParam("currencyCode", currency != null ? currency : "USD")
                .toUriString();

        JsonNode response = executeGetRequest(url, apiKey, API_HOST);
        List<CarDTO> carList = new ArrayList<>();

        if (response != null && response.has("data")) {
            JsonNode searchResults = response.path("data").path("search_results");

            if (searchResults.isArray()) {
                for (JsonNode carNode : searchResults) {
                    CarDTO dto = new CarDTO();

                    // Mapeo desde vehicle_info
                    JsonNode vInfo = carNode.path("vehicle_info");
                    dto.setCarName(vInfo.path("v_name").asText());
                    dto.setImageUrl(vInfo.path("image_url").asText());
                    dto.setTransmission(vInfo.path("transmission").asText());
                    dto.setSeats(vInfo.path("seats").asInt());

                    // Suma de maletas (vienen como strings en tu JSON)
                    int big = vInfo.path("suitcases").path("big").asInt(0);
                    int small = vInfo.path("suitcases").path("small").asInt(0);
                    dto.setBags(big + small);

                    // Mapeo desde pricing_info
                    JsonNode pInfo = carNode.path("pricing_info");
                    dto.setPrice(pInfo.path("drive_away_price").asDouble());
                    dto.setCurrency(pInfo.path("currency").asText());

                    // Mapeo desde supplier_info
                    dto.setVendorName(carNode.path("supplier_info").path("name").asText());

                    // URL de reserva
                    dto.setDeeplink(carNode.path("forward_url").asText());

                    carList.add(dto);
                }
            }
        }
        return carList;
    }
}