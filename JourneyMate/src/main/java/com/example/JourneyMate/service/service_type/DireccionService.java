package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.DireccionEntity;

import java.util.List;

public interface DireccionService {
    List<DireccionEntity> findAll();

    DireccionEntity findById(Integer idDireccion);

    DireccionEntity save(DireccionEntity direccion);

    void deleteById(Integer idDireccion);
}
