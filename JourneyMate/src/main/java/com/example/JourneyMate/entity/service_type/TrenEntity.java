package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tren", schema = "journeymate")
@Data

public class TrenEntity {
    private LocalDate fechaLlegada;
    private String compañia;
    private String origen;
    private String destino;
    private LocalDate fechaSalida;
}
