package com.example.JourneyMate.service.impl.search;

import com.example.JourneyMate.dao.search.HistorialBusquedaRepository;
import com.example.JourneyMate.entity.search.HistorialBusquedaEntity;
import com.example.JourneyMate.service.search.HistorialBusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialBusquedaServiceImpl implements HistorialBusquedaService {

    @Autowired
    private HistorialBusquedaRepository historialBusquedaRepository;

    @Override
    public List<HistorialBusquedaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return historialBusquedaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<HistorialBusquedaEntity> findAll() {
        return historialBusquedaRepository.findAll();
    }

    @Override
    public HistorialBusquedaEntity findById(Integer idHistorial) {
        return historialBusquedaRepository.findById(idHistorial).orElse(null);
    }

    @Override
    public HistorialBusquedaEntity save(HistorialBusquedaEntity historial) {
        return historialBusquedaRepository.save(historial);
    }

    @Override
    public void deleteById(Integer idHistorial) {
        historialBusquedaRepository.deleteById(idHistorial);
    }
}
