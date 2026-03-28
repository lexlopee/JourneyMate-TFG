package com.example.JourneyMate.dto.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ServicioTuristicoRequestDTO {

    private String tipo; // HOTEL, VUELO, CRUCERO, VTC, ACTIVIDAD, APARTAMENTO

    private String nombre;
    private BigDecimal precioBase;
    private String descripcion;
    private Integer estrellas; // solo para hoteles

    // Dirección opcional
    private String descripcion_direccion;
    private Double latitud;
    private Double longitud;

    // Datos específicos según tipo
    private String origen;
    private String destino;
    private String compania;
    private String naviera;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaSalida;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegreso;

    private String marca;
    private String modelo;
    private String distancia;
}
