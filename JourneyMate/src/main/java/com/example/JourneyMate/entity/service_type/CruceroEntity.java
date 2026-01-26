package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "crucero", schema = "journeymate")
@Data

public class CruceroEntity {
    private String puertoLlegada;
    private String puertoSalida;
    private String naviera;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
}
