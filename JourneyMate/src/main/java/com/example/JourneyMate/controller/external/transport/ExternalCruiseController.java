package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.example.JourneyMate.service.external.transport.ICruiseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cruises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalCruiseController {

    private final ICruiseService cruiseService;

    /**
     * PASO 1a: Obtener catálogo de destinos (CARIB, EUROP, etc.)
     * Se usa para llenar el primer desplegable del formulario.
     */
    @GetMapping("/destinations")
    public ResponseEntity<JsonNode> getDestinations() {
        return ResponseEntity.ok(cruiseService.getDestinations());
    }

    /**
     * PASO 1b: Obtener catálogo de puertos (MIA, BCN, etc.)
     * Se usa para llenar el segundo desplegable.
     */
    @GetMapping("/ports")
    public ResponseEntity<JsonNode> getPorts() {
        return ResponseEntity.ok(cruiseService.getPorts());
    }

    /**
     * PASO 2: Búsqueda final de cruceros
     * Recibe los códigos seleccionados por el usuario.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CruiseDTO>> searchCruises(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String destination,
            @RequestParam String departurePort,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String country) {

        return ResponseEntity.ok(cruiseService.searchCruises(
                startDate, endDate, destination, departurePort, currency, country
        ));
    }
}