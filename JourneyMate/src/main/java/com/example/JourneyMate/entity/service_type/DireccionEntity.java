package com.example.JourneyMate.entity.service_type;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "direccion", schema = "journeymate")
@Data
public class DireccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_direccion;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private ServicioTuristicoEntity servicio;

    private String calle;
    private String numero;
    private String ciudad;
    private Double latitud;
    private Double longitud;
}

