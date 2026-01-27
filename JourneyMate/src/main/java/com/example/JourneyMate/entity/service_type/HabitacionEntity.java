package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "habitacion", schema = "journeymate")
@Data
public class HabitacionEntity extends HotelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @ManyToOne
    @JoinColumn(name = "id_hotel")
    private HotelEntity hotel;

    private String tipo;
    private BigDecimal precio_noche;
    private Integer capacidad;
}
