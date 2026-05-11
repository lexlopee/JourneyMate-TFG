package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.example.JourneyMate.service.external.transport.ICruiseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar servicios externos
 * relacionados con cruceros.
 * <p>
 * Permite consultar destinos, puertos y buscar cruceros
 * disponibles según diferentes criterios.
 */
@RestController
@RequestMapping("/api/v1/cruises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalCruiseController {

    /**
     * Servicio encargado de la integración con APIs externas
     * de cruceros.
     */
    private final ICruiseService cruiseService;

    /**
     * Obtiene el catálogo de destinos disponibles para cruceros.
     * <p>
     * Se utiliza para llenar el selector de destinos
     * en el formulario de búsqueda.
     *
     * @return catálogo de destinos disponibles
     */
    @GetMapping("/destinations")
    public ResponseEntity<JsonNode> getDestinations() {

        return ResponseEntity.ok(
                cruiseService.getDestinations()
        );
    }

    /**
     * Obtiene el catálogo de puertos disponibles para cruceros.
     * <p>
     * Se utiliza para llenar el selector de puertos
     * en el formulario de búsqueda.
     *
     * @return catálogo de puertos disponibles
     */
    @GetMapping("/ports")
    public ResponseEntity<JsonNode> getPorts() {

        return ResponseEntity.ok(
                cruiseService.getPorts()
        );
    }

    /**
     * Busca cruceros disponibles según los filtros indicados.
     *
     * @param startDate fecha inicial del viaje
     * @param endDate fecha final del viaje
     * @param destination código del destino seleccionado
     * @param departurePort código del puerto de salida
     * @param currency código de moneda utilizado
     * @param country país utilizado para la búsqueda
     * @return lista de cruceros encontrados
     */
    @GetMapping("/search")
    public ResponseEntity<List<CruiseDTO>> searchCruises(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String destination,
            @RequestParam String departurePort,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String country) {

        return ResponseEntity.ok(
                cruiseService.searchCruises(
                        startDate,
                        endDate,
                        destination,
                        departurePort,
                        currency,
                        country
                )
        );
    }
}