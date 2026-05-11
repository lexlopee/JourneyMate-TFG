package com.example.JourneyMate.external.accommodations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la información básica de un hotel.
 * Se utiliza para transferir datos simplificados de alojamientos
 * entre la capa de integración externa y la aplicación.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    /**
     * Identificador único del hotel.
     */
    private String hotelId;

    /**
     * Nombre del hotel.
     */
    private String nombre;

    /**
     * Precio final del alojamiento.
     */
    private Double precio;

    /**
     * Código de la moneda del precio (EUR, USD, etc.).
     */
    private String moneda;

    /**
     * Puntuación media del hotel basada en reseñas.
     */
    private Double calificacion;

    /**
     * Descripción textual de la puntuación del hotel
     * (por ejemplo: "Excelente", "Muy bueno").
     */
    private String reviewWord;

    /**
     * Clase o categoría del alojamiento (por ejemplo 3, 4 o 5 estrellas).
     */
    private Integer propertyClass;

    /**
     * URL de la imagen principal del hotel.
     */
    private String urlFoto;

    /**
     * Latitud geográfica del hotel.
     */
    private Double latitud;

    /**
     * Longitud geográfica del hotel.
     */
    private Double longitud;

    /**
     * Precio original antes de descuentos o promociones.
     */
    private Double precioOriginal;
}