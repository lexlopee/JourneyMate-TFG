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
    @Column(name = "telefono", length = 13)
    private String telefono;
    @Column(name = "contraseña", length = 30)
    private String contrasenia;
    @Column(name = "nombre", length = 30)
    private String nombre;
    @Column(name = "primer_apellido", length = 30)
    private String primerApellido;
    @Column(name = "segundo_apellido", length = 30)
    private String segundoApellido;
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(name = "email", length = 30)
    private String email;
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;
}
