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

/**
 * Servicio encargado de consumir la API externa de cruceros (CruiseWave API).
 *
 * Permite obtener destinos, puertos y realizar búsquedas de cruceros,
 * así como mapear la respuesta a DTOs internos del sistema.
 */
@Service
public class CruiseServiceImpl extends BaseExternalService implements ICruiseService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${cruisewave.api.host}")
    private String apiHost;

    public CruiseServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Obtiene la lista de destinos disponibles en la API de cruceros.
     *
     * @return nodo JSON con los destinos
     */
    @Override
    public JsonNode getDestinations() {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://cruisewave-api.p.rapidapi.com/metadata/destinations")
                .toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    /**
     * Obtiene la lista de puertos disponibles en la API de cruceros.
     *
     * @return nodo JSON con los puertos
     */
    @Override
    public JsonNode getPorts() {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://cruisewave-api.p.rapidapi.com/metadata/ports")
                .toUriString();

        return executeGetRequest(url, apiKey, apiHost);
    }

    /**
     * Busca cruceros disponibles según los filtros proporcionados.
     *
     * @param startDate fecha de inicio del crucero
     * @param endDate fecha de fin del crucero
     * @param destination destino del crucero
     * @param departurePort puerto de salida
     * @param currency moneda (ej: EUR, USD)
     * @param country país de referencia
     * @return lista de cruceros en formato {@link CruiseDTO}
     */
    @Override
    public List<CruiseDTO> searchCruises(String startDate,
                                         String endDate,
                                         String destination,
                                         String departurePort,
                                         String currency,
                                         String country) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://cruisewave-api.p.rapidapi.com/cruise-search")
                .queryParam("start_date", startDate)
                .queryParam("end_date", endDate)
                .queryParam("destination", destination)
                .queryParam("departure_port", departurePort);

        if (currency != null && !currency.isEmpty()) {
            builder.queryParam("currency", currency);
        }
        if (country != null && !country.isEmpty()) {
            builder.queryParam("country", country);
        }

        JsonNode body = executeGetRequest(builder.toUriString(), apiKey, apiHost);

        List<CruiseDTO> cruiseList = new ArrayList<>();

        JsonNode cruisesArray = body.path("data")
                .path("cruiseSearch")
                .path("results")
                .path("cruises");

        if (cruisesArray.isArray()) {

            int limite = 20;
            int contador = 0;

            for (JsonNode node : cruisesArray) {
                if (contador >= limite) break;

                cruiseList.add(mapToDTO(node));
                contador++;
            }
        }

        return cruiseList;
    }

    /**
     * Convierte un nodo JSON de crucero en un objeto {@link CruiseDTO}.
     *
     * @param node nodo JSON de la API
     * @return DTO con la información del crucero
     */
    private CruiseDTO mapToDTO(JsonNode node) {

        JsonNode master = node.path("masterSailing").path("itinerary");
        JsonNode pricing = node.path("lowestPriceSailing");

        List<String> images = new ArrayList<>();
        master.path("media").path("images").forEach(img ->
                images.add("https://www.royalcaribbean.com" + img.path("path").asText())
        );

        List<CruiseDTO.ItinerarioDTO> paradas = new ArrayList<>();
        master.path("days").forEach(day -> {

            JsonNode firstPort = day.path("ports").get(0);

            paradas.add(CruiseDTO.ItinerarioDTO.builder()
                    .dia(day.path("number").asInt())
                    .tipo(day.path("type").asText())
                    .puerto(firstPort.path("port").path("name").asText())
                    .region(firstPort.path("port").path("region").asText())
                    .llegada(firstPort.path("arrivalTime").asText("---"))
                    .salida(firstPort.path("departureTime").asText("---"))
                    .build());
        });

        List<CruiseDTO.CabinaDTO> cabinas = new ArrayList<>();
        pricing.path("stateroomClassPricing").forEach(c -> {
            if (!c.path("price").isMissingNode()) {
                cabinas.add(CruiseDTO.CabinaDTO.builder()
                        .tipo(c.path("stateroomClass").path("id").asText())
                        .precio(c.path("price").path("value").asDouble())
                        .build());
            }
        });

        return CruiseDTO.builder()
                .id(node.path("id").asText())
                .nombreCrucero(master.path("name").asText())
                .nombreBarco(master.path("ship").path("name").asText())
                .noches(master.path("sailingNights").asInt())
                .puertoSalida(master.path("departurePort").path("name").asText())
                .destino(master.path("destination").path("name").asText())
                .fechaSalida(pricing.path("startDate").asText())
                .fechaLlegada(pricing.path("endDate").asText())
                .precioDesde(pricing.path("lowestStateroomClassPrice").path("price").path("value").asDouble())
                .tasasYImpuestos(pricing.path("taxesAndFees").path("value").asDouble())
                .moneda(pricing.path("lowestStateroomClassPrice").path("price").path("currency").path("code").asText("EUR"))
                .linkReserva(pricing.path("bookingLink").asText())
                .imagenPrincipal(!images.isEmpty() ? images.get(0) : "")
                .galeriaImagenes(images)
                .paradas(paradas)
                .cabinas(cabinas)
                .build();
    }
}