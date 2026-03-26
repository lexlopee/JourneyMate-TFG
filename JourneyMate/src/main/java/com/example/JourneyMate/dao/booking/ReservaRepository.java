package com.example.JourneyMate.dao.booking;

import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    // ⭐ Query directa que construye el DTO sin cargar relaciones anidadas
    // Evita el ERR_INCOMPLETE_CHUNKED_ENCODING causado por la herencia JOINED
    // de ServicioTuristicoEntity al serializar el grafo completo de entidades
    @Query("""
        SELECT new com.example.JourneyMate.dto.reserva.ReservaListDTO(
            r.idReserva,
            s.nombre,
            r.precioTotal,
            e.nombre,
            t.nombre,
            r.fechaReserva
        )
        FROM ReservaEntity r
        JOIN r.servicio s
        JOIN r.estado e
        JOIN r.tipoReserva t
        WHERE r.usuario.idUsuario = :idUsuario
    """)
    List<ReservaListDTO> findDTOsByUsuarioId(@Param("idUsuario") Integer idUsuario);

    // Métodos originales — no tocar
    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);
    List<ReservaEntity> findByEstadoNombre(String nombre);
    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}