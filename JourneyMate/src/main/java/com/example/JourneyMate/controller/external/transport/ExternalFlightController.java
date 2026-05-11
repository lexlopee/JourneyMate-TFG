package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.example.JourneyMate.external.flights.FlightDetailsDTO;
import com.example.JourneyMate.service.external.transport.IFlightService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar servicios externos
 * relacionados con vuelos.
 * <p>
 * Permite buscar ubicaciones, consultar vuelos disponibles
 * y obtener detalles específicos de un vuelo.
 */
@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalFlightController {

    /**
     * Servicio encargado de la integración con APIs externas
     * de vuelos y aerolíneas.
     */
    private final IFlightService flightService;

    /**
     * Busca ubicaciones relacionadas con aeropuertos o ciudades.
     * <p>
     * Utilizado principalmente para autocompletado
     * en formularios de búsqueda de vuelos.
     *
     * @param query texto ingresado por el usuario
     * @return información de ubicaciones encontradas
     */
    @GetMapping("/location")
    public ResponseEntity<JsonNode> searchLocation(
            @RequestParam String query) {

        return ResponseEntity.ok(
                flightService.searchLocation(query)
        );
    }

    /**
     * Busca vuelos disponibles según los parámetros indicados.
     *
     * @param fromId identificador del aeropuerto o ciudad de origen
     * @param toId identificador del aeropuerto o ciudad de destino
     * @param departDate fecha de salida
     * @param returnDate fecha de regreso
     * @param stops cantidad de escalas permitidas
     * @param pageNo número de página de resultados
     * @param adults cantidad de adultos
     * @param childrenAge edades de los niños, si aplica
     * @param sort criterio de ordenamiento de resultados
     * @param cabinClass clase de cabina seleccionada
     * @param currencyCode código de moneda utilizado
     * @return lista de vuelos encontrados
     */
    @GetMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam String fromId,
            @RequestParam String toId,
            @RequestParam String departDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(defaultValue = "none") String stops,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "1") Integer adults,
            @RequestParam(required = false) String childrenAge,
            @RequestParam(defaultValue = "BEST") String sort,
            @RequestParam(defaultValue = "ECONOMY") String cabinClass,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        return ResponseEntity.ok(
                flightService.searchFlights(
                        fromId,
                        toId,
                        departDate,
                        returnDate,
                        stops,
                        pageNo,
                        adults,
                        childrenAge,
                        sort,
                        cabinClass,
                        currencyCode
                )
        );
    }

    /**
     * Obtiene información detallada de un vuelo específico.
     *
     * @param token identificador único del vuelo
     * @param currencyCode código de moneda utilizado
     * @return detalles completos del vuelo
     */
    @GetMapping("/details")
    public ResponseEntity<FlightDetailsDTO> getFlightDetails(
            @RequestParam String token,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        FlightDetailsDTO flightDetails =
                flightService.getFlightDetails(token, currencyCode);

        return ResponseEntity.ok(flightDetails);
    }
}