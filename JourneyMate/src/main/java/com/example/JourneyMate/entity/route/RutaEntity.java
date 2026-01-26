package com.example.JourneyMate.entity.route;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ruta", schema = "journeymate")
@Data
public class RutaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ruta;

    private String nombre;
    private LocalDate fecha_creacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "ruta")
    private List<RutaPuntoInteresEntity> puntos;
}

