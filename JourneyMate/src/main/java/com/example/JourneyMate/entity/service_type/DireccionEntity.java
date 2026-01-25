package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "direccion", schema = "journeymate")
@Data
public class DireccionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_direccion;
    private String calle;
    private String numero;
    private String ciudad;
    private double latitud;
    private double longitud;
}
