package com.example.JourneyMate.dao.route;

import com.example.JourneyMate.entity.route.RutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutaRepository extends JpaRepository<RutaEntity, Integer> {
    List<RutaEntity> findByUsuario_IdUsuario(Integer idUsuario);
}
