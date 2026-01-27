package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "crucero", schema = "journeymate")
@Data

public class CruceroEntity extends ServicioTuristicoEntity {
    private String puertoLlegada;
    private String puertoSalida;
    private String naviera;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
}
