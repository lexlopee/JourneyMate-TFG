package com.example.JourneyMate.external.cars;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {
    private String id;
    private String nombreVehiculo; // Ej: "Volkswagen Polo"
    private String tipo;           // Ej: "Económico"
    private String proveedor;
    private Double precioTotal;
    private String moneda;
    private String urlFoto;
    private Integer plazas;        // Número de asientos
    private String transmision;    // "Manual" o "Automático"
}