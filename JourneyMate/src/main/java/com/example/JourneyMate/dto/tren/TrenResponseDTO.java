package com.example.JourneyMate.dto.tren;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrenResponseDTO {
    private Integer idServicio;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
    private String compañia;
    private String origen;
    private String destino;
}
