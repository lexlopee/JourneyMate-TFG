package com.example.JourneyMate.controller.external.maps;

import com.example.JourneyMate.external.maps.MapPredictionDTO;
import com.example.JourneyMate.service.external.MapsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para que React no tenga problemas de CORS
public class ExternalMapsController {

    private final MapsService mapsService;

    // 1. Endpoint para el buscador (lista de ciudades)
    @GetMapping("/autocomplete")
    public ResponseEntity<List<MapPredictionDTO>> autocomplete(@RequestParam String query) {
        return ResponseEntity.ok(mapsService.getAutocomplete(query));
    }

    // 2. Endpoint para obtener lat/long al seleccionar una ciudad
    @GetMapping("/details/{placeId}")
    public ResponseEntity<JsonNode> getDetails(@PathVariable String placeId) {
        return ResponseEntity.ok(mapsService.getPlaceDetails(placeId));
    }

    // 3. Endpoint para puntos turísticos cercanos
    @GetMapping("/nearby")
    public ResponseEntity<JsonNode> getNearby(@RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(mapsService.getNearbyTouristPoints(lat, lon));
    }
}