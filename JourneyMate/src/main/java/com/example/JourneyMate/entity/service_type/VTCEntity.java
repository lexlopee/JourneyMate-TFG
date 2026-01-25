package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vtc", schema = "journeymate")
@Data

public class VTCEntity {
    private LocalDate horaSalida;
    private String distancia;
    private String marca;
    private String modelo;
    private String precio;
    private LocalDate horaLlegada;
}
