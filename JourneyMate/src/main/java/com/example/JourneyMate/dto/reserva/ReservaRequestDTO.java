package com.example.JourneyMate.dto.reserva;

import com.example.JourneyMate.dto.service.ServicioTuristicoRequestDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReservaRequestDTO {
    private Integer idUsuario;
    private Integer idTipoReserva;
    private Integer idEstado;
    private BigDecimal precioTotal;

    private ServicioTuristicoRequestDTO servicio;
}
