package com.example.JourneyMate.dto.vtc;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class VTCRequestDTO {

    private LocalTime horaSalida;
    private LocalTime horaLlegada;
    private BigDecimal precio;
    private String distancia;
    private String marca;
    private String modelo;
}
