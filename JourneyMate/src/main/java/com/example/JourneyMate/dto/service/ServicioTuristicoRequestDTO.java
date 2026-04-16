package com.example.JourneyMate.dto.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ServicioTuristicoRequestDTO {

    private String tipo;

    private String nombre;
    private BigDecimal precioBase;
    private String descripcion;
    private Integer estrellas;

    // Dirección
    private String descripcion_direccion;
    private Double latitud;
    private Double longitud;

    // Transporte
    private String origen;
    private String destino;
    private String compania;
    private String naviera;
    private String puertoSalida;
    private String puertoLlegada;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaLlegada;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaSalida;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegreso;

    private String marca;
    private String modelo;

    // ✅ CORREGIDO: era String, la tabla VTC tiene distancia NUMERIC(6,2)
    private BigDecimal distancia;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime horaSalida;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime horaLlegada;
}