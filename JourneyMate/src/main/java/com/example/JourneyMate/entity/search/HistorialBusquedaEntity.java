package com.example.JourneyMate.entity.search;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "historial_busqueda", schema = "journeymate")
@Data
public class HistorialBusquedaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_historial;

    private LocalDate fecha;
    private String termino;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;
}

