package com.example.JourneyMate.entity.recommendation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recomedacion", schema = "journeymate")

public class RecomendacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion")
    private Integer idRecomendacion;
    private Integer idElemento;
    private Integer idUsuario;
    private BigDecimal score;
}
