package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "vuelo", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class VueloEntity extends ServicioTuristicoEntity {

    private String compañia;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegada;
    private String origen;
    private String destino;
}


