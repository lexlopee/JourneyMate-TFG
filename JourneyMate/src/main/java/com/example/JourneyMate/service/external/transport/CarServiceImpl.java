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

/**
 * Servicio encargado de consumir la API externa de alquiler de coches (Booking Cars).
 *
 * Permite buscar ubicaciones de recogida y realizar búsquedas de vehículos disponibles
 * en función de distintos parámetros de viaje.
 */
@Service
public class CarServiceImpl extends BaseExternalService implements ICarService {

    @Value("${rapidapi.key}")
    private String apiKey;

    private final String API_HOST = "booking-com18.p.rapidapi.com";

    public CarServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Busca ubicaciones disponibles para alquiler de coches.
     *
     * @param query texto de búsqueda (ciudad, aeropuerto, etc.)
     * @param languageCode idioma de la respuesta (ej: es, en)
     * @param countryFlag código de país para filtrar resultados
     * @return lista de ubicaciones disponibles en formato {@link CarLocationDTO}
     */
    @Override
    public List<CarLocationDTO> searchCarLocation(String query,
                                                  String languageCode,
                                                  String countryFlag) {

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

    /**
     * Busca coches de alquiler disponibles según los parámetros del viaje.
     *
     * @param pickUpId identificador del punto de recogida
     * @param dropOffId identificador del punto de devolución (puede ser null)
     * @param pDate fecha de recogida
     * @param pTime hora de recogida
     * @param dDate fecha de devolución
     * @param dTime hora de devolución
     * @param age edad del conductor
     * @param currency moneda (ej: EUR, USD)
     * @param carType tipo de coche (opcional)
     * @return lista de coches disponibles en formato {@link CarDTO}
     */
    @Override
    public List<CarDTO> searchCars(String pickUpId,
                                   String dropOffId,
                                   String pDate,
                                   String pTime,
                                   String dDate,
                                   String dTime,
                                   Integer age,
                                   String currency,
                                   String carType) {

        String effectiveDropOff = (dropOffId != null && !dropOffId.isEmpty())
                ? dropOffId
                : pickUpId;

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