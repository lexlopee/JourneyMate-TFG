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
    private String calle;
    private String numero;
    private String ciudad;
    private Double latitud;
    private Double longitud;

    // Datos específicos según tipo
    private String origen;
    private String destino;
    private String compañia;
    private String naviera;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaSalida;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaLlegada;

    private String marca;
    private String modelo;
    private String distancia;
}
