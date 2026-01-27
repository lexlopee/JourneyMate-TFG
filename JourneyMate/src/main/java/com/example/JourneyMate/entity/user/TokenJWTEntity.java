package com.example.JourneyMate.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "token_jwt", schema = "journeymate")
@Data
@NoArgsConstructor
public class TokenJWTEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Integer idToken;
    @Column(name = "fecha_expiacion", nullable = false)
    private LocalDate fecha_expiacion;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fecha_creacion;
    @Column(name = "token", length = 100)
    private String token;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity id_usuario;
}
