package com.example.JourneyMate.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "rol", schema = "journeymate")
@Data
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "rol")
    private List<UsuarioEntity> id_usuarios;
}
