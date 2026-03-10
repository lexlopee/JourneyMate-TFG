package com.example.JourneyMate.dao.booking;

import com.example.JourneyMate.entity.booking.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoRepository extends JpaRepository<EstadoEntity, Integer> {

    Optional<EstadoEntity> findByNombreIgnoreCase(String nombre);
}
