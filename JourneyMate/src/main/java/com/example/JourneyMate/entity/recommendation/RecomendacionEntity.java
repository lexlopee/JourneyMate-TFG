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
    private Integer id_recomendacion;
    private Integer id_elemento;
    private Integer id_usuario;
    private BigDecimal score;
}
