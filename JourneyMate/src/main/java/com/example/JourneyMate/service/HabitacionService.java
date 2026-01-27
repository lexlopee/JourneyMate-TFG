package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.HabitacionEntity;

import java.util.List;

public interface HabitacionService {

    List<HabitacionEntity> findAll();

    HabitacionEntity findById(Integer idHabitacion);

    HabitacionEntity save(HabitacionEntity habitacion);

    void deleteById(Integer idHabitacion);

    List<HabitacionEntity> findByHotelIdServicio(Integer idHotel);

    List<HabitacionEntity> findByCapacidadGreaterThanEqual(Integer capacidad);
}
