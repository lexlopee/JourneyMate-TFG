package com.example.JourneyMate.dao.service_type;

import com.example.JourneyMate.entity.service_type.HabitacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitacionRepository extends JpaRepository<HabitacionEntity, Integer> {

    List<HabitacionEntity> findByHotel_IdHotel(Integer idHotel);

    List<HabitacionEntity> findByCapacidadGreaterThanEqual(Integer capacidad);
}
