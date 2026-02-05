package com.example.JourneyMate.external.activities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private String nombre;
    private String descripcion;
    private Double precio;
    private String moneda;
    private Double calificacion;
    private String urlFoto;
    private String idActividad;
}