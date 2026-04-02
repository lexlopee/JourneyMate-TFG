package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vtc", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
@EqualsAndHashCode(callSuper = true)
public class VTCEntity extends ServicioTuristicoEntity {

    @Column(name = "hora_salida")
    private LocalDate horaSalida; // Coincide con SQL DATE

    @Column(name = "hora_llegada")
    private LocalDate horaLlegada; // Coincide con SQL DATE

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "distancia")
    private BigDecimal distancia; // Coincide con NUMERIC(6,2)

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;
}