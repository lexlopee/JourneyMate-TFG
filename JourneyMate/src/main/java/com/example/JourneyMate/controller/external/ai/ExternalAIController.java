package com.example.JourneyMate.controller.external.ai;

import com.example.JourneyMate.service.external.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador encargado de gestionar funcionalidades basadas
 * en inteligencia artificial dentro de la aplicación.
 * <p>
 * Permite generar recomendaciones de viaje e itinerarios
 * personalizados utilizando servicios externos de IA.
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalAIController {
    /**
     * Servicio encargado de generar recomendaciones
     * e itinerarios mediante inteligencia artificial.
     */
    private final AIRecommendationService aiService;
    /**
     * Genera recomendaciones de viaje personalizadas
     * según las preferencias y presupuesto del usuario.
     *
     * @param pref preferencias o intereses del usuario
     * @param budget presupuesto disponible para el viaje
     * @return recomendación generada por inteligencia artificial
     */
    @GetMapping("/recommend")
    public ResponseEntity<String> getRecommendation(
            @RequestParam String pref,
            @RequestParam String budget) {
        return ResponseEntity.ok(aiService.getRecommendation(pref, budget));
    }
    /**
     * Genera un itinerario personalizado a partir
     * de la consulta proporcionada por el usuario.
     *
     * @param query descripción o solicitud del itinerario
     * @return itinerario generado por inteligencia artificial
     */
    @GetMapping("/itinerary")
    public ResponseEntity<String> getItinerary(@RequestParam String query) {
        return ResponseEntity.ok(aiService.getItinerary(query));
    }
}
