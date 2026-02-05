package com.example.JourneyMate.external.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightDTO {
    private String aerolinea;
    private String logoUrl;
    private String origen;
    private String destino;
    private String horaSalida;
    private String horaLlegada;
    private Double precio;
    private String moneda;
}