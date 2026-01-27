package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.service.booking.ReservaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservaServiceImpl implements ReservaService {
    @Override
    public List<ReservaEntity> findAll() {
        return List.of();
    }

    @Override
    public Optional<ReservaEntity> findById(Integer idReserva) {
        return Optional.empty();
    }

    @Override
    public ReservaEntity crear(ReservaEntity reserva) {
        return null;
    }

    @Override
    public ReservaEntity actualizar(Integer idReserva, ReservaEntity reserva) {
        return null;
    }

    @Override
    public void deleteById(Integer idReserva) {

    }

    @Override
    public boolean existsById(Integer idReserva) {
        return false;
    }

    @Override
    public List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return List.of();
    }

    @Override
    public List<ReservaEntity> findByEstadoNombre(String estado) {
        return List.of();
    }

    @Override
    public List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin) {
        return List.of();
    }
}
