package com.example.JourneyMate.dto.pago;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PagoRequestDTO {
    // Pago de UNA reserva
    private Integer idReserva;

    // ⭐ Pago de VARIAS reservas a la vez (Pagar Todo)
    private List<Integer> reservaIds;

    private Integer idMetodo;
    private String estadoPago;
    private LocalDate fechaPago;
}