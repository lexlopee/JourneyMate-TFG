package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.example.JourneyMate.service.external.BaseExternalService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class CruiseServiceImpl extends BaseExternalService implements ICruiseService {

    public CruiseServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${cruisewave.api.host}")
    private String apiHost;

    @Override
    public JsonNode getDestinations() {
        String url = UriComponentsBuilder.fromHttpUrl("https://cruisewave-api.p.rapidapi.com/metadata/destinations")
                .toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    @Override
    public JsonNode getPorts() {
        String url = UriComponentsBuilder.fromHttpUrl("https://cruisewave-api.p.rapidapi.com/metadata/ports")
                .toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    @Override
    public List<CruiseDTO> searchCruises(String startDate, String endDate, String destination,
                                         String departurePort, String currency, String country) {

        // 1. Construcción de la URL con parámetros opcionales
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://cruisewave-api.p.rapidapi.com/cruise-search")
                .queryParam("start_date", startDate)
                .queryParam("end_date", endDate)
                .queryParam("destination", destination)
                .queryParam("departure_port", departurePort);

        if (currency != null) builder.queryParam("currency", currency);
        if (country != null) builder.queryParam("country", country);

        // 2. Usamos el metodo de la clase base
        JsonNode body = executeGetRequest(builder.toUriString(), apiKey, apiHost);

        List<CruiseDTO> cruiseList = new ArrayList<>();

        if (body != null) {
            // Accedemos a la ruta profunda del JSON de CruiseWave
            JsonNode cruisesArray = body.path("data")
                    .path("cruiseSearch")
                    .path("results")
                    .path("cruises");

            if (cruisesArray.isArray()) {
                for (JsonNode node : cruisesArray) {
                    cruiseList.add(mapToDTO(node));
                }
            }
        }
        return cruiseList;
    }

    private CruiseDTO mapToDTO(JsonNode node) {
        CruiseDTO dto = new CruiseDTO();

        // 1. Información del Itinerario y Barco
        JsonNode itinerary = node.path("masterSailing").path("itinerary");
        dto.setNombreCrucero(itinerary.path("name").asText("Crucero Desconocido"));
        dto.setNombreBarco(itinerary.path("ship").path("name").asText("Barco N/A"));
        dto.setNoches(itinerary.path("sailingNights").asInt(0));
        dto.setPuertoSalida(itinerary.path("departurePort").path("name").asText("N/A"));

        // 2. Información de Fecha y Precio
        JsonNode lowestSailing = node.path("lowestPriceSailing");
        dto.setFechaSalida(lowestSailing.path("startDate").asText("N/A"));

        // 3. Extracción del Precio
        JsonNode priceNode = lowestSailing.path("lowestStateroomClassPrice").path("price");
        dto.setPrecioDesde(priceNode.path("value").asDouble(0.0));
        dto.setMoneda(priceNode.path("currency").path("code").asText("USD"));

        return dto;
    }
}