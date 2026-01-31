package com.example.JourneyMate.dto.mapper.booking;

import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.dto.reserva.ReservaResponseDTO;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;

public class ReservaMapper {

    public static ReservaEntity toEntity(ReservaRequestDTO dto) {
        ReservaEntity entity = new ReservaEntity();

        // Relación con Usuario
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(dto.getIdUsuario());
        entity.setUsuario(usuario);

        // Relación con Servicio Turístico
        ServicioTuristicoEntity servicio = new ServicioTuristicoEntity();
        servicio.setIdServicio(dto.getIdServicio());
        entity.setServicio(servicio);

        // Relación con Estado
        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(dto.getIdEstado());
        entity.setEstado(estado);

        // Relación con TipoReserva
        TipoReservaEntity tipo = new TipoReservaEntity();
        tipo.setIdTipoReserva(dto.getIdTipoReserva());
        entity.setTipoReserva(tipo);

        entity.setPrecio_total(dto.getPrecioTotal());
        entity.setFechaReserva(dto.getFechaReserva());

        return entity;
    }

    public static ReservaResponseDTO toDTO(ReservaEntity entity) {
        ReservaResponseDTO dto = new ReservaResponseDTO();

        dto.setIdReserva(entity.getIdReserva());
        dto.setIdUsuario(entity.getUsuario().getIdUsuario());
        dto.setIdServicio(entity.getServicio().getIdServicio());
        dto.setIdEstado(entity.getEstado().getIdEstado());
        dto.setIdTipoReserva(entity.getTipoReserva().getIdTipoReserva());
        dto.setPrecioTotal(entity.getPrecio_total());
        dto.setFechaReserva(entity.getFechaReserva());

        return dto;
    }
}
