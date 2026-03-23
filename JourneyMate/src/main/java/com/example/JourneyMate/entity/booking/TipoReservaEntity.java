package com.example.JourneyMate.entity.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "tipo_reserva", schema = "journeymate")
public class TipoReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_reserva")
    private Integer idTipoReserva;

    // CORREGIDO: era "name" sin @Column, la columna en BBDD se llama "nombre"
    @Column(name = "nombre")
    private String nombre;
}