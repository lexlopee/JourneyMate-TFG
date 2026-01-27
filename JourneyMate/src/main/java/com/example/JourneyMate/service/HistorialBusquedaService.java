package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.search.HistorialBusquedaEntity;

import java.util.List;

public interface HistorialBusquedaService {
    List<HistorialBusquedaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<HistorialBusquedaEntity> findAll();

    HistorialBusquedaEntity findById(Integer idHistorial);

    HistorialBusquedaEntity save(HistorialBusquedaEntity historial);

    void deleteById(Integer idHistorial);
}
