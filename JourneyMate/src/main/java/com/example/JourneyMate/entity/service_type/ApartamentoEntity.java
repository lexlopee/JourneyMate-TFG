package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "apartamento", schema = "journeymate")
@PrimaryKeyJoinColumn(name = "id_servicio")
@Data
public class ApartamentoEntity extends ServicioTuristicoEntity {

    private String descripcion;
}
