package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vtc", schema = "journeymate")
@Data

public class VTCEntity extends ServicioTuristicoEntity {
    private LocalDate horaSalida;
    private String distancia;
    private String marca;
    private String modelo;
    private String precio;
    private LocalDate horaLlegada;
}
