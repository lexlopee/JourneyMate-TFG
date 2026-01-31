package com.example.JourneyMate.dto.ruta;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RutaResponseDTO {

    private Integer idRuta;
    private String nombre;
    private LocalDate fechaCreacion;
    private Integer idUsuario;
    private Integer numeroPuntos;
}
