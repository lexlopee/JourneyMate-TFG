package com.example.JourneyMate.external.maps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa una predicción de ubicación en el sistema de mapas.
 * Se utiliza para autocompletado de lugares.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapPredictionDTO {

    /**
     * Descripción completa de la predicción (texto mostrado al usuario).
     */
    private String description;

    /**
     * Identificador único del lugar (placeId).
     */
    private String placeId;

    /**
     * Texto principal de la predicción (nombre del lugar).
     */
    private String mainText;
}