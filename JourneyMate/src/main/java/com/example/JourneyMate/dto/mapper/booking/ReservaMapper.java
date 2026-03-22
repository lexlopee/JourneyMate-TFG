package com.example.JourneyMate.dto.mapper.booking;

import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;

import java.time.LocalDate;

public class ReservaMapper {

    public static ReservaEntity toEntity(ReservaRequestDTO dto, UsuarioEntity usuario) {
        ReservaEntity r = new ReservaEntity();

        // Usuario
        r.setUsuario(usuario);

        // Servicio

        // Tipo de reserva
        TipoReservaEntity tipo = new TipoReservaEntity();
        tipo.setIdTipoReserva(dto.getIdTipoReserva());
        r.setTipoReserva(tipo);

        // Precio
        r.setPrecioTotal(dto.getPrecioTotal());

        // Fecha
        r.setFechaReserva(LocalDate.now());

        return r;
    }
}



