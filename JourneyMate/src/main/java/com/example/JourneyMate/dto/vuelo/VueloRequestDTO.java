package com.example.JourneyMate.dto.vuelo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VueloRequestDTO {

    private String compañia;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
    private String origen;
    private String destino;
}
