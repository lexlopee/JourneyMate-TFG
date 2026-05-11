package com.example.JourneyMate.controller.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.external.activities.ActivityDetailsDTO;
import com.example.JourneyMate.service.external.activities.IActivityService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador encargado de gestionar actividades turísticas externas.
 * <p>
 * Permite buscar ubicaciones, consultar actividades disponibles
 * y obtener detalles específicos de una actividad.
 */
@RestController
@RequestMapping("/api/v1/activities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExternalActivityController {
    /**
     * Servicio encargado de la integración con proveedores
     * externos de actividades turísticas.
     */
    private final IActivityService activityService;

    /**
     * Busca una ubicación para obtener el identificador
     * de destino utilizado por la API externa.
     *
     * @param query nombre o término de búsqueda de la ubicación
     * @return información de la ubicación encontrada
     */
    @GetMapping("/location")
    public ResponseEntity<JsonNode> getLocation(@RequestParam String query) {
        return ResponseEntity.ok(activityService.searchLocation(query));
    }

    /**
     * Busca actividades disponibles para un destino específico.
     *
     * @param id identificador del destino (UFI)
     * @param startDate fecha inicial de búsqueda
     * @param endDate fecha final de búsqueda
     * @param sortBy criterio de ordenamiento de resultados
     * @param page número de página de resultados
     * @param currencyCode código de moneda utilizado
     * @return lista de actividades encontradas
     */
    @GetMapping("/search")
    public ResponseEntity<List<ActivityDTO>> searchActivities(
            @RequestParam String id,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "trending") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        List<ActivityDTO> result = activityService.searchActivities(
                id, startDate, endDate, sortBy, page, currencyCode, "es", null
        );

        System.out.println("Resultados encontrados: " + (result != null ? result.size() : "null"));

        return ResponseEntity.ok(result);
    }
    /**
     * Obtiene los detalles completos de una actividad específica.
     *
     * @param slug identificador único de la actividad
     * @param currencyCode código de moneda utilizado
     * @return detalles completos de la actividad
     */
    @GetMapping("/details")
    public ResponseEntity<ActivityDetailsDTO> getActivityDetails(
            @RequestParam String slug,
            @RequestParam(defaultValue = "EUR") String currencyCode) {
        return ResponseEntity.ok(activityService.getActivityDetails(slug, currencyCode, "es"));
    }
}