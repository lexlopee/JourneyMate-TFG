package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.entity.booking.EstadoEntity;

import java.util.List;
import java.util.Optional;

public interface EstadoService {
    List<EstadoEntity> findAll();
    Optional<EstadoEntity> findById(Integer idEstado);
    EstadoEntity crear(EstadoEntity estado);
    EstadoEntity actualizar(Integer idEstado, EstadoEntity estado);
    void deleteById(Integer idEstado);
    boolean existsById(Integer idEstado);
    EstadoEntity findByNombre(String nombre); // Muy útil en reservas
}
