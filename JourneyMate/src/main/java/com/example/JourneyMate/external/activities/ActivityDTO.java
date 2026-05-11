package com.example.JourneyMate.external.activities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa una actividad turística en formato simplificado.
 * Se utiliza para listados o vistas resumidas de actividades.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {

    /**
     * Nombre de la actividad.
     */
    private String nombre;

    /**
     * Descripción breve de la actividad.
     */
    private String descripcion;

    /**
     * Precio de la actividad.
     */
    private Double precio;

    /**
     * Código de la moneda del precio (EUR, USD, etc.).
     */
    private String moneda;

    /**
     * Puntuación media de la actividad basada en reseñas.
     */
    private Double calificacion;

    /**
     * URL de la imagen principal de la actividad.
     */
    private String urlFoto;

    /**
     * Identificador único de la actividad.
     */
    private String idActividad;

    /**
     * Slug SEO-friendly de la actividad.
     */
    private String slug;
}