package com.example.JourneyMate.dto.crucero;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CruceroResponseDTO {

    private Integer idServicio;
    private String puertoLlegada;
    private String puertoSalida;
    private String naviera;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
}
