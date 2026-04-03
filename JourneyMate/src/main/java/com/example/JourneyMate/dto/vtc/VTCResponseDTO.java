package com.example.JourneyMate.dto.vtc;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VTCResponseDTO {
    private Integer idServicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaSalida;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaLlegada;

    private BigDecimal precio;
    private BigDecimal distancia;
    private String marca;
    private String modelo;
}