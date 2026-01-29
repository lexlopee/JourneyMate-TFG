package com.example.JourneyMate.external.cruises;

import lombok.Data;

@Data
public class CruiseDTO {
    private String nombreCrucero;
    private String nombreBarco;
    private String fechaSalida;
    private Integer noches;
    private String puertoSalida;
    private Double precioDesde;
    private String moneda;
}