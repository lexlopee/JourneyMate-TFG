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
    private Integer id_habitacion;

    @ManyToOne
    @JoinColumn(name = "id_hotel")
    private HotelEntity hotel;

    private String tipo;
    private BigDecimal precio_noche;
    private Integer capacidad;
}
