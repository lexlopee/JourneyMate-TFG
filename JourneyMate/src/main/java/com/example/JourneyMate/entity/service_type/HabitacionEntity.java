package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "habitacion", schema = "journeymate")
@Data
public class HabitacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @ManyToOne
    @JoinColumn(name = "id_hotel", nullable = false)
    private HotelEntity hotel;

    private String tipo;

    @Column(name = "precio_noche")
    private BigDecimal precioNoche;

    private Integer capacidad;
}

