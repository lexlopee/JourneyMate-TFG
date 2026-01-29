package com.example.JourneyMate.external.flights;

import lombok.Data;

@Data
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