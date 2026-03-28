package com.example.JourneyMate.dto.vuelo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VueloRequestDTO {
    private String compania;
    private LocalDate fechaSalida;
    private LocalDate fechaRegreso;
    private String origen;
    private String destino;
}
