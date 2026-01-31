package com.example.JourneyMate.dto.crucero;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CruceroRequestDTO {

    private String puertoLlegada;
    private String puertoSalida;
    private String naviera;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
}
