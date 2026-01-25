package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "apatamento", schema = "journeymate")

public class ApartamentoEntity {
    private String descripcion;
}
