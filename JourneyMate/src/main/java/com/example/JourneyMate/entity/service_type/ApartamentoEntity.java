package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "apartamento", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class ApartamentoEntity extends ServicioTuristicoEntity {

    // descripcion SÍ existe en la tabla apartamento del SQL
    @Column(name = "descripcion")
    private String descripcion;
}