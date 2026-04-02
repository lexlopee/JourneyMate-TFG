package com.example.JourneyMate.dto.vtc;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VTCRequestDTO {
    private LocalDate horaSalida;
    private LocalDate horaLlegada;
    private BigDecimal precio;
    private BigDecimal distancia;
    private String marca;
    private String modelo;
}