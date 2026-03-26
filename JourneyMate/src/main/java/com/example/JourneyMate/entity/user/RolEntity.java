package com.example.JourneyMate.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "rol", schema = "journeymate")
@Data
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;

    @OneToMany(mappedBy = "rol")
    @ToString.Exclude  // ⭐ ESTO es todo lo que necesitas añadir
    private List<UsuarioEntity> usuarios;
}
