package com.example.JourneyMate.entity.route;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "ruta_punto_interes", schema = "journeymate")

public class RutaPuntoInteresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_punto;
    private Integer id_ruta;
    private int orden;
}
