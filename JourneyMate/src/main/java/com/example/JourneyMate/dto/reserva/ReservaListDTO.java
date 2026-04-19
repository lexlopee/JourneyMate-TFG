package com.example.JourneyMate.dto.reserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaListDTO {
    private Integer idReserva;
    private String servicioNombre;
    private BigDecimal precioTotal;
    private String estadoNombre;
    private String tipoReservaNombre;
    private LocalDate fechaReserva;
    // ✅ NUEVOS: necesarios para el botón "Repetir reserva"
    private Integer idServicio;      // id del servicio original en BBDD
    private Integer idTipoReserva;   // para saber qué modal abrir
    private BigDecimal precioBase;   // precio_base del servicio
}