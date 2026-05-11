package com.example.JourneyMate.external.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado que representa un vuelo.
 * Se utiliza para listados o vistas resumidas de opciones de vuelos.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightDTO {

    /**
     * Token único de identificación del vuelo.
     */
    private String token;

    /**
     * Nombre de la aerolínea.
     */
    private String aerolinea;

    /**
     * URL del logo de la aerolínea.
     */
    private String logoUrl;

    /**
     * Aeropuerto o ciudad de origen.
     */
    private String origen;

    /**
     * Aeropuerto o ciudad de destino.
     */
    private String destino;

    /**
     * Hora de salida del vuelo.
     */
    private String horaSalida;

    /**
     * Hora de llegada del vuelo.
     */
    private String horaLlegada;

    /**
     * Precio del vuelo.
     */
    private Double precio;

    /**
     * Código de la moneda del precio (EUR, USD, etc.).
     */
    private String moneda;

    /**
     * Número de escalas del vuelo.
     */
    private Integer stops;

    /**
     * Duración total del vuelo (formato legible, ej: "2h 35m").
     */
    private String duracion;

    /**
     * Clase de cabina (economy, business, etc.).
     */
    private String cabinClass;
}