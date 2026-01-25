package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "actividad", schema = "journeymate")
public class ActividadEntity {
    private String descripcion;
}
