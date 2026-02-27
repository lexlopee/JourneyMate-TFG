package com.example.JourneyMate.external.cruises;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CruiseDTO {
    private String nombreCrucero;
    private String nombreBarco;
    private String fechaSalida;
    private Integer noches;
    private String puertoSalida;
    private Double precioDesde;
    private String moneda;
}