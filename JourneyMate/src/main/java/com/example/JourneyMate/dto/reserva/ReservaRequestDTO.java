package com.example.JourneyMate.dto.reserva;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReservaRequestDTO {
    private BigDecimal precioTotal;
    private Integer idTipoReserva;
    private String nombreServicio;
    private Integer idUsuario; // ⭐ AÑADIDO
}
