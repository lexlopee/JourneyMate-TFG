package com.example.JourneyMate.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "usuario", schema = "journeymate")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String contrasenia;
    private String email;
    private LocalDate fechaRegistro;
    private LocalDate fechaNacimiento;
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;
}
