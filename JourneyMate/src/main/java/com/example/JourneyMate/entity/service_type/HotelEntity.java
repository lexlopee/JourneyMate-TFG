package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "hotel", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class HotelEntity extends ServicioTuristicoEntity {

    private Integer estrellas;
    private String descripcion;

    @OneToMany(mappedBy = "hotel")
    private List<HabitacionEntity> habitaciones;
}

