package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "vuelo", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
@EqualsAndHashCode(callSuper = true)
public class VueloEntity extends ServicioTuristicoEntity {

    @Column(name = "compañia")
    private String compania;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "fecha_regreso")   // ← corregido: en BD es fecha_llegada, no fecha_regreso
    private LocalDate fechaRegreso;

    @Column(name = "origen")
    private String origen;

    @Column(name = "destino")
    private String destino;
}