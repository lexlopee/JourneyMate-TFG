package com.example.JourneyMate.service.impl.search;

import com.example.JourneyMate.entity.search.HistorialBusquedaEntity;
import com.example.JourneyMate.service.search.HistorialBusquedaService;

import java.util.List;

public class HistorialBusquedaServiceImpl implements HistorialBusquedaService {
    @Override
    public List<HistorialBusquedaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return List.of();
    }

    @Override
    public List<HistorialBusquedaEntity> findAll() {
        return List.of();
    }

    @Override
    public HistorialBusquedaEntity findById(Integer idHistorial) {
        return null;
    }

    @Override
    public HistorialBusquedaEntity save(HistorialBusquedaEntity historial) {
        return null;
    }

    @Override
    public void deleteById(Integer idHistorial) {

    }
}
