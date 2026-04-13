package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    List<ReservaEntity> findAll();

    Optional<ReservaEntity> findById(Integer idReserva);

    // ✅ Usamos solo este método para crear reservas completas
    ReservaEntity crearCompleta(ReservaRequestDTO dto);

    ReservaEntity actualizar(Integer idReserva, ReservaEntity reserva);

    void deleteById(Integer idReserva);

    boolean existsById(Integer idReserva);

    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<ReservaEntity> findByEstadoNombre(String estado);

    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);

    List<ReservaListDTO> findDTOsByUsuarioId(Integer idUsuario);

    List<ReservaListDTO> findHistorialByUsuarioId(Integer idUsuario);
}
