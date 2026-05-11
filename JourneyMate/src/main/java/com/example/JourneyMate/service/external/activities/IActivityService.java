package com.example.JourneyMate.service.external.activities;

import com.example.JourneyMate.external.activities.ActivityDTO;
import com.example.JourneyMate.external.activities.ActivityDetailsDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Interfaz que define las operaciones para el consumo
 * de la API externa de actividades turísticas (Booking Attractions).
 *
 * Proporciona métodos para búsqueda de ubicaciones, actividades
 * y obtención de detalles de actividades.
 */
public interface IActivityService {

    /**
     * Busca ubicaciones turísticas a partir de un texto de consulta.
     *
     * @param query nombre de la ciudad o destino
     * @return nodo JSON con los resultados de ubicaciones
     */
    JsonNode searchLocation(String query);

    /**
     * Busca actividades turísticas según los parámetros proporcionados.
     *
     * @param id identificador de la ubicación
     * @param startDate fecha de inicio de la búsqueda
     * @param endDate fecha de fin de la búsqueda
     * @param sortBy criterio de ordenación (ej: trending, price, rating)
     * @param page número de página de resultados
     * @param currencyCode código de moneda (ej: EUR, USD)
     * @param languageCode código de idioma (ej: es, en)
     * @param typeFilters filtros por tipo de actividad
     * @return lista de actividades en formato {@link ActivityDTO}
     */
    List<ActivityDTO> searchActivities(
            String id,
            String startDate,
            String endDate,
            String sortBy,
            Integer page,
            String currencyCode,
            String languageCode,
            String typeFilters
    );

    /**
     * Obtiene los detalles completos de una actividad turística.
     *
     * @param id identificador de la actividad
     * @param currencyCode código de moneda
     * @param languageCode código de idioma
     * @return detalles de la actividad en formato {@link ActivityDetailsDTO}
     */
    ActivityDetailsDTO getActivityDetails(String id, String currencyCode, String languageCode);
}