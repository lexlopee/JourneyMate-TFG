package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.booking.ReservaEntity;

import java.util.List;

public interface ReservaService {
    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);
    List<ReservaEntity> finByEstadoNombre(String estado);
    List<ReservaEntity> findByAll();
    ReservaEntity findById(Integer idReserva);
    ReservaEntity save(ReservaEntity reserva);
    void deleteById(Integer idReserva);
}
