package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coche", schema = "journeymate")  // ✅ tabla renombrada de vtc a coche
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
@EqualsAndHashCode(callSuper = true)
public class CocheEntity extends ServicioTuristicoEntity {

    // ✅ TIMESTAMP en BD → LocalDateTime en Java
    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Column(name = "hora_llegada")
    private LocalDateTime horaLlegada;

    // ✅ NUMERIC(10,2) → BigDecimal
    @Column(name = "precio")
    private BigDecimal precio;

    // ✅ NUMERIC(6,2) → BigDecimal
    @Column(name = "distancia")
    private BigDecimal distancia;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;
}