package com.example.JourneyMate.entity.service;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "servicio_turistico", schema = "journeymate")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class ServicioTuristicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio_base")
    private BigDecimal precioBase;
}

