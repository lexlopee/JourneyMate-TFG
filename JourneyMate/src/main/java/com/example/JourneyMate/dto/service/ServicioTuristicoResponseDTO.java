package com.example.JourneyMate.dto.service;

import lombok.Data;

@Data
public class ServicioTuristicoResponseDTO {

    private Integer idServicio;
    private String nombre;
    private Double precioBase;
    private String tipo;
    private String descripcion;
    private Integer estrellas;
}
