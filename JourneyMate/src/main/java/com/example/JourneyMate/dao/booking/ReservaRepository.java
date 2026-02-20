package com.example.JourneyMate.dao.booking;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {
    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<ReservaEntity> findByEstadoNombre(String nombre);

    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);

}
