package com.example.JourneyMate.external.cars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa una ubicación disponible para alquiler de coches.
 * Puede corresponder a aeropuertos, ciudades o distritos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarLocationDTO {

    /**
     * Identificador único de la localización (hash Base64).
     */
    private String id;

    /**
     * Nombre del lugar (por ejemplo: Aeropuerto JFK).
     */
    private String name;

    /**
     * Ciudad donde se encuentra la localización.
     */
    private String city;

    /**
     * País donde se encuentra la localización.
     */
    private String country;

    /**
     * Tipo de localización (airport, city, district).
     */
    private String type;

    /**
     * Código IATA del aeropuerto (puede ser null si no aplica).
     */
    private String iataCode;
}