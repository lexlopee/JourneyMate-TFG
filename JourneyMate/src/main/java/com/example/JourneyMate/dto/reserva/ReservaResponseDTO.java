package com.example.JourneyMate.dto.reserva;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReservaResponseDTO {

    private Integer idReserva;
    private Integer idUsuario;
    private Integer idServicio;
    private Integer idEstado;
    private Integer idTipoReserva;
    private BigDecimal precioTotal;
    private LocalDate fechaReserva;
}
