package com.example.JourneyMate.dto.habitacion;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HabitacionRequestDTO {

    private Integer idHotel;
    private String tipo;
    private BigDecimal precioNoche;
    private Integer capacidad;
}
