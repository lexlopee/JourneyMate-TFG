package com.example.JourneyMate.dto.vtc;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; // Sincronizado con SQL DATE

@Data
public class VTCResponseDTO {
    private Integer idServicio;
    private LocalDate horaSalida;  // Antes LocalTime
    private LocalDate horaLlegada; // Antes LocalTime
    private BigDecimal precio;
    private BigDecimal distancia;  // Sincronizado con NUMERIC(6,2)
    private String marca;
    private String modelo;
}