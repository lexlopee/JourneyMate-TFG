package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity,Integer> {
    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);
    List<ReservaEntity> finByEstadoNombre(String estado);
}
