package com.example.JourneyMate.dao.booking;

import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    // ── "Mis Reservas": solo PENDIENTE (sin pagar) ───────────────────────────
    @Query("""
                SELECT new com.example.JourneyMate.dto.reserva.ReservaListDTO(
                    r.idReserva,
                    s.nombre,
                    r.precioTotal,
                    e.nombre,
                    t.nombre,
                    r.fechaReserva,
                    s.idServicio,
                    t.idTipoReserva,
                    s.precioBase
                )
                FROM ReservaEntity r
                JOIN r.servicio s
                JOIN r.estado e
                JOIN r.tipoReserva t
                WHERE r.usuario.idUsuario = :idUsuario
                AND UPPER(e.nombre) = 'PENDIENTE'
                ORDER BY r.fechaReserva DESC
            """)
    List<ReservaListDTO> findDTOsByUsuarioId(@Param("idUsuario") Integer idUsuario);

    // ── "Historial": TODAS las reservas del usuario sin filtro de estado ─────
    // Así aparece todo: pendiente, confirmada, completada, cancelada
    @Query("""
                SELECT new com.example.JourneyMate.dto.reserva.ReservaListDTO(
                    r.idReserva,
                    s.nombre,
                    r.precioTotal,
                    e.nombre,
                    t.nombre,
                    r.fechaReserva,
                    s.idServicio,
                    t.idTipoReserva,
                    s.precioBase
                )
                FROM ReservaEntity r
                JOIN r.servicio s
                JOIN r.estado e
                JOIN r.tipoReserva t
                WHERE r.usuario.idUsuario = :idUsuario
                ORDER BY r.fechaReserva DESC
            """)
    List<ReservaListDTO> findHistorialByUsuarioId(@Param("idUsuario") Integer idUsuario);

    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);
    List<ReservaEntity> findByEstadoNombre(String nombre);
    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}