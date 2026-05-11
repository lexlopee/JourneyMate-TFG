package com.example.JourneyMate.external.activities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa los detalles de una actividad turística.
 * Contiene información descriptiva, precios, valoraciones e inclusiones.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDetailsDTO {

    /**
     * Identificador único de la actividad.
     */
    private String idActividad;

    /**
     * Nombre de la actividad.
     */
    private String nombre;

    /**
     * Descripción extensa de la actividad.
     */
    private String descripcionLarga;

    /**
     * Descripción breve de la actividad.
     */
    private String shortDescription;

    /**
     * Lista de URLs de imágenes asociadas a la actividad.
     */
    private List<String> fotos;

    /**
     * Precio de la actividad.
     */
    private Double precio;

    /**
     * Código de la moneda del precio (EUR, USD, etc.).
     */
    private String moneda;

    /**
     * Lista de elementos incluidos en la actividad.
     */
    private List<String> whatsIncluded;

    /**
     * Lista de elementos no incluidos en la actividad.
     */
    private List<String> notIncluded;

    /**
     * Indica si la actividad permite cancelación gratuita.
     */
    private boolean hasFreeCancellation;

    /**
     * Valoración media de la actividad basada en reseñas.
     */
    private Double averageRating;

    /**
     * Número total de reseñas recibidas.
     */
    private Integer totalReviews;
}