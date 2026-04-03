package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hotel", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class HotelEntity extends ServicioTuristicoEntity {

    // Estas columnas SÍ existen en la tabla hotel del SQL
    @Column(name = "estrellas")
    private Integer estrellas;

    @Column(name = "descripcion")
    private String descripcion;

}