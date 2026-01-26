package com.example.JourneyMate.entity.service;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "servicio_turistico", schema = "journeymate")
@Data
public class ServicioTuristicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;
    private String name;
    private BigDecimal precio_base;
}
