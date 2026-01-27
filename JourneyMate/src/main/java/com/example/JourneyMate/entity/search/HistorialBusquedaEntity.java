package com.example.JourneyMate.entity.search;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "historial_busqueda", schema = "journeymate")
@Data
public class HistorialBusquedaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    private LocalDate fecha;
    private String termino;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;
}

