package com.example.JourneyMate.external.activities;

import lombok.Data;

@Data
public class ActivityDTO {
    private String nombre;
    private String descripcion;
    private Double precio;
    private String moneda;
    private Double calificacion;
    private String urlFoto;
    private String idActividad;
}