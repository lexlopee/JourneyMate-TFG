package com.example.JourneyMate.dao.booking;

import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    // ── "Mis Reservas" activas: solo PENDIENTE (sin pagar) ──────────────────
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

    // ── "Historial": CONFIRMADA + COMPLETADA + CANCELADA ────────────────────
    // (excluye PENDIENTE porque esas ya aparecen en "Mis Reservas")
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
                AND UPPER(e.nombre) IN ('CONFIRMADA', 'COMPLETADA', 'CANCELADA')
                ORDER BY r.fechaReserva DESC
            """)
    List<ReservaListDTO> findHistorialByUsuarioId(@Param("idUsuario") Integer idUsuario);

    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);
    List<ReservaEntity> findByEstadoNombre(String nombre);
    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}