package com.example.JourneyMate.external.accommodations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {
    private String nombre;
    private Double precio;
    private String moneda;
    private Double calificacion;
    private String reviewWord;
    private String urlFoto;
    private Double latitud;
    private Double longitud;
}