package com.example.JourneyMate.dao.booking;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    // ── "Mis Reservas": solo PENDIENTE ────────────────────────────────────────
    // Usamos SQL nativo para evitar problemas de tipos JPQL con BigDecimal/NUMERIC
    @Query(value = """
                SELECT
                    r.id_reserva       AS idReserva,
                    s.nombre           AS servicioNombre,
                    r.precio_total     AS precioTotal,
                    e.nombre           AS estadoNombre,
                    t.nombre           AS tipoReservaNombre,
                    r.fecha_reserva    AS fechaReserva,
                    s.id_servicio      AS idServicio,
                    t.id_tipo_reserva  AS idTipoReserva,
                    s.precio_base      AS precioBase
                FROM journeymate.reserva r
                JOIN journeymate.servicio_turistico s ON r.id_servicio = s.id_servicio
                JOIN journeymate.estado e ON r.id_estado = e.id_estado
                JOIN journeymate.tipo_reserva t ON r.id_tipo_reserva = t.id_tipo_reserva
                WHERE r.id_usuario = :idUsuario
                AND UPPER(e.nombre) = 'PENDIENTE'
                ORDER BY r.fecha_reserva DESC
            """, nativeQuery = true)
    List<Object[]> findRawDTOsByUsuarioId(@Param("idUsuario") Integer idUsuario);

    // ── "Historial": TODAS las reservas sin filtro de estado ─────────────────
    @Query(value = """
                SELECT
                    r.id_reserva       AS idReserva,
                    s.nombre           AS servicioNombre,
                    r.precio_total     AS precioTotal,
                    e.nombre           AS estadoNombre,
                    t.nombre           AS tipoReservaNombre,
                    r.fecha_reserva    AS fechaReserva,
                    s.id_servicio      AS idServicio,
                    t.id_tipo_reserva  AS idTipoReserva,
                    s.precio_base      AS precioBase
                FROM journeymate.reserva r
                JOIN journeymate.servicio_turistico s ON r.id_servicio = s.id_servicio
                JOIN journeymate.estado e ON r.id_estado = e.id_estado
                JOIN journeymate.tipo_reserva t ON r.id_tipo_reserva = t.id_tipo_reserva
                WHERE r.id_usuario = :idUsuario
                ORDER BY r.fecha_reserva DESC
            """, nativeQuery = true)
    List<Object[]> findRawHistorialByUsuarioId(@Param("idUsuario") Integer idUsuario);

    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<ReservaEntity> findByEstadoNombre(String nombre);

    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}