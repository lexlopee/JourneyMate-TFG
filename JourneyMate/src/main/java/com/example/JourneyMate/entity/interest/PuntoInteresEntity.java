package com.example.JourneyMate.entity.interest;

import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "punto_interes", schema = "journeymate")
@Data
public class PuntoInteresEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPunto;

    private String ciudad;
    private String nombre;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaEntity categoria;

    @OneToMany(mappedBy = "puntoInteres")
    private List<RutaPuntoInteresEntity> rutas;
}

