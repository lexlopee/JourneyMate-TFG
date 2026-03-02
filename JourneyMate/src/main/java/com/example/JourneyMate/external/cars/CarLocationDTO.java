package com.example.JourneyMate.external.cars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarLocationDTO {
    private String id;          // El hash Base64 (ej: eyJsYXRp...)
    private String name;        // Nombre del lugar (ej: Aeropuerto JFK)
    private String city;        // Ciudad (ej: New York)
    private String country;     // País (ej: United States)
    private String type;        // Tipo: airport, city, district
    private String iataCode;    // Código IATA (ej: JFK) - puede ser null
}