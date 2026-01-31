package com.example.JourneyMate.dto.ruta;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RutaRequestDTO {

    private String nombre;
    private LocalDate fechaCreacion;
    private Integer idUsuario;
}
