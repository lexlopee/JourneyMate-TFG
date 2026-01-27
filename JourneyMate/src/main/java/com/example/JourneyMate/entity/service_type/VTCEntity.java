package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "vtc", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class VTCEntity extends ServicioTuristicoEntity {

    private LocalTime horaSalida;
    private LocalTime horaLlegada;

    private BigDecimal precio;
    private String distancia;
    private String marca;
    private String modelo;
}

