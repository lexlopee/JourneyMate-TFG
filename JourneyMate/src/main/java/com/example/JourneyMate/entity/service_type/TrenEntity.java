package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "tren", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class TrenEntity extends ServicioTuristicoEntity {

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "fecha_llegada")
    private LocalDate fechaLlegada;

    @Column(name = "compañia")
    private String compania;

    @Column(name = "origen")
    private String origen;

    @Column(name = "destino")
    private String destino;
}