package com.example.JourneyMate.entity.route;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ruta_punto_interes", schema = "journeymate")
@Data
public class RutaPuntoInteresEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRutaPuntoInteres;

    @ManyToOne
    @JoinColumn(name = "id_ruta", nullable = false)
    private RutaEntity ruta;

    @ManyToOne
    @JoinColumn(name = "id_punto_interes", nullable = false)
    private PuntoInteresEntity puntoInteres;

    private Integer orden;
}

