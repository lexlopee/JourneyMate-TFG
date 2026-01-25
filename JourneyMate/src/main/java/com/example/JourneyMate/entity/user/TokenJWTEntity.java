package com.example.JourneyMate.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "token_jwt", schema = "journeymate")
@Data
public class TokenJWTEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_token;

    private LocalDate fecha_expiacion;
    private LocalDate fecha_creacion;
    private String token;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;
}
