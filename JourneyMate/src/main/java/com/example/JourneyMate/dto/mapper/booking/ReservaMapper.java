package com.example.JourneyMate.dto.mapper.booking;

import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;

import java.time.LocalDate;

public class ReservaMapper {

    public static ReservaEntity toEntity(ReservaRequestDTO dto, UsuarioEntity usuario, ServicioTuristicoEntity servicio,
                                         TipoReservaEntity tipo, EstadoEntity estado) {
        ReservaEntity r = new ReservaEntity();

        r.setUsuario(usuario);
        r.setServicio(servicio);   // ⭐ AQUÍ SE SETEA EL SERVICIO
        r.setTipoReserva(tipo);
        r.setEstado(estado);
        r.setPrecioTotal(dto.getPrecioTotal());
        r.setFechaReserva(LocalDate.now());

        return r;
    }
}
