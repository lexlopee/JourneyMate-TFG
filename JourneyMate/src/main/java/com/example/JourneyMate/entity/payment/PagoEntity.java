package com.example.JourneyMate.entity.payment;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "pago", schema = "journeymate")
@Data
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pago;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private ReservaEntity reserva;

    @ManyToOne
    @JoinColumn(name = "id_metodo")
    private MetodoEntity metodo;

    private String estado_pago;
    private LocalDate fecha_pago;
}
