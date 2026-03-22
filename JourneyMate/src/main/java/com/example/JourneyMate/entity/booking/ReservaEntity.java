package com.example.JourneyMate.entity.booking;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reserva", schema = "journeymate")
@Data
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private ServicioTuristicoEntity servicio;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoEntity estado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_reserva", nullable = false)
    private TipoReservaEntity tipoReserva;

    @Column(name = "precio_total")
    private BigDecimal precioTotal;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;
}

