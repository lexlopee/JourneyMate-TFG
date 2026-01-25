package com.example.JourneyMate.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "rol", schema = "journeymate")
@Data
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_rol;

    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "rol")
    private List<UsuarioEntity> usuarios;
}
