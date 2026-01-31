package com.example.JourneyMate.dto.habitacion;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HabitacionResponseDTO {

    private Integer idHabitacion;
    private Integer idHotel;
    private String tipo;
    private BigDecimal precioNoche;
    private Integer capacidad;
}
