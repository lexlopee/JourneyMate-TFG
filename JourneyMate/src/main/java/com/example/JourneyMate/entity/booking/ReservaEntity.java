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
    private Integer id_reserva;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private ServicioTuristicoEntity servicio;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoEntity estado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_reserva")
    private TipoReservaEntity tipoReserva;

    private BigDecimal precio_total;
    private LocalDate fecha_reserva;
}
