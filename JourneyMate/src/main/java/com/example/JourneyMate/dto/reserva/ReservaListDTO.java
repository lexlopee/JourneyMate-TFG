package com.example.JourneyMate.dto.reserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor  // ⭐ Necesario para el "new" en la query JPQL
public class ReservaListDTO {
    private Integer idReserva;
    private String servicioNombre;
    private BigDecimal precioTotal;
    private String estadoNombre;
    private String tipoReservaNombre;
    private LocalDate fechaReserva;
}