package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.booking.EstadoEntity;

import java.util.List;

public interface EstadoService {
    List<EstadoEntity> findAll();
    EstadoEntity findById(Integer idEstado);
    EstadoEntity save(EstadoEntity estado);
    void deleteById(Integer idEstado);
}
