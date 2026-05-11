package com.example.JourneyMate.controller.external.maps;

import com.example.JourneyMate.external.maps.MapPredictionDTO;
import com.example.JourneyMate.service.external.MapsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador encargado de gestionar funcionalidades relacionadas
 * con mapas y geolocalización.
 * <p>
 * Permite buscar ubicaciones, obtener coordenadas geográficas
 * y consultar puntos turísticos cercanos mediante servicios externos.
 */
@RestController
@RequestMapping("/api/v1/maps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para que React no tenga problemas de CORS
public class ExternalMapsController {
    /**
     * Servicio encargado de la integración con APIs externas
     * de mapas y geolocalización.
     */
    private final MapsService mapsService;

    /**
     * Obtiene sugerencias de ubicaciones a partir de un texto ingresado.
     * <p>
     * Utilizado principalmente para autocompletado en buscadores
     * de ciudades o destinos.
     *
     * @param query texto ingresado por el usuario
     * @return lista de ubicaciones sugeridas
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<MapPredictionDTO>> autocomplete(@RequestParam String query) {
        return ResponseEntity.ok(mapsService.getAutocomplete(query));
    }

    /**
     * Obtiene información detallada de una ubicación,
     * incluyendo coordenadas geográficas.
     *
     * @param placeId identificador único del lugar
     * @return información detallada del lugar seleccionado
     */
    @GetMapping("/details/{placeId}")
    public ResponseEntity<JsonNode> getDetails(@PathVariable String placeId) {
        return ResponseEntity.ok(mapsService.getPlaceDetails(placeId));
    }

    /**
     * Obtiene puntos turísticos cercanos a una ubicación específica.
     *
     * @param lat latitud de la ubicación
     * @param lon longitud de la ubicación
     * @return lista de puntos turísticos cercanos
     */
    @GetMapping("/nearby")
    public ResponseEntity<JsonNode> getNearby(@RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(mapsService.getNearbyTouristPoints(lat, lon));
    }
}