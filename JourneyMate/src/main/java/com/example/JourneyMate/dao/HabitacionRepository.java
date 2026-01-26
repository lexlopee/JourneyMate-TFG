package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.service_type.HabitacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitacionRepository extends JpaRepository<HabitacionEntity, Integer> {

    List<HabitacionEntity> findByHotelIdServicio(Integer idHotel);

    List<HabitacionEntity> findByCapacidadGreaterThanEqual(Integer capacidad);
}
