package com.example.JourneyMate.entity.service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "servicio_turistico", schema = "journeymate")
public class ServicioTuristicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_servicio;
    private String name;
    private BigDecimal precio_base;
}
