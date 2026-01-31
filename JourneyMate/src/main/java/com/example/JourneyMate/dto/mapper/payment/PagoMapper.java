package com.example.JourneyMate.dto.mapper.payment;

import com.example.JourneyMate.dto.pago.PagoRequestDTO;
import com.example.JourneyMate.dto.pago.PagoResponseDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.payment.MetodoEntity;
import com.example.JourneyMate.entity.payment.PagoEntity;

public class PagoMapper {

    public static PagoEntity toEntity(PagoRequestDTO dto) {
        PagoEntity entity = new PagoEntity();

        // Relación con Reserva
        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdReserva(dto.getIdReserva());
        entity.setReserva(reserva);

        // Relación con Método de pago
        MetodoEntity metodo = new MetodoEntity();
        metodo.setIdMetodo(dto.getIdMetodo());
        entity.setMetodo(metodo);

        entity.setEstado_pago(dto.getEstadoPago());
        entity.setFecha_pago(dto.getFechaPago());

        return entity;
    }

    public static PagoResponseDTO toDTO(PagoEntity entity) {
        PagoResponseDTO dto = new PagoResponseDTO();

        dto.setIdPago(entity.getIdPago());
        dto.setIdReserva(entity.getReserva().getIdReserva());
        dto.setIdMetodo(entity.getMetodo().getIdMetodo());
        dto.setEstadoPago(entity.getEstado_pago());
        dto.setFechaPago(entity.getFecha_pago());

        return dto;
    }
}
