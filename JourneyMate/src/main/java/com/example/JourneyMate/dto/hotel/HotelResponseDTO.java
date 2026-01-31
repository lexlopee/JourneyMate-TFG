package com.example.JourneyMate.dto.hotel;

import lombok.Data;

@Data
public class HotelResponseDTO {

    private Integer idServicio;
    private Integer estrellas;
    private String descripcion;
    private Integer numeroHabitaciones;
}
