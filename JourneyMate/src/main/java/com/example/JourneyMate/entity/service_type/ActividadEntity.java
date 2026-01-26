package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "actividad", schema = "journeymate")
public class ActividadEntity extends ServicioTuristicoEntity {
    private String descripcion;
}
