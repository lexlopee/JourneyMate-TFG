package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.DireccionRepository;
import com.example.JourneyMate.entity.service_type.DireccionEntity;
import com.example.JourneyMate.service.service_type.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionServiceImpl implements DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    @Override
    public List<DireccionEntity> findAll() {
        return direccionRepository.findAll();
    }

    @Override
    public DireccionEntity findById(Integer idDireccion) {
        return direccionRepository.findById(idDireccion).orElse(null);
    }

    @Override
    public DireccionEntity save(DireccionEntity direccion) {
        return direccionRepository.save(direccion);
    }

    @Override
    public void deleteById(Integer idDireccion) {
        direccionRepository.deleteById(idDireccion);
    }
}
