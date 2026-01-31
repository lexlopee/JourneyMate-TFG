package com.example.JourneyMate.dto.pago;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PagoRequestDTO {

    private Integer idReserva;
    private Integer idMetodo;
    private String estadoPago;
    private LocalDate fechaPago;
}
