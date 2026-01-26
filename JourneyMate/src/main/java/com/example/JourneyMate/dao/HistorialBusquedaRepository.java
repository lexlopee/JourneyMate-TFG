package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.search.HistorialBusquedaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialBusquedaRepository extends JpaRepository<HistorialBusquedaEntity,Integer> {
    List<HistorialBusquedaEntity> findByUsuarioIdUsuario(Integer idUsuario);
}
