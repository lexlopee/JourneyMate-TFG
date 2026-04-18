package com.example.JourneyMate.dto.reserva;

import com.example.JourneyMate.dto.service.ServicioTuristicoRequestDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReservaRequestDTO {
    private Integer idUsuario;
    private Integer idTipoReserva;
    private Integer idEstado;
    private BigDecimal precioTotal;

    // ✅ NUEVO: fecha de inicio del servicio reservado (check-in, salida, etc.)
    // Si viene null se usa la fecha actual como fallback
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaServicio;

    private ServicioTuristicoRequestDTO servicio;
}