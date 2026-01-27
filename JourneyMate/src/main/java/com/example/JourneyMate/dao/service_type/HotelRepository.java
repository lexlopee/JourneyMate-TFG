package com.example.JourneyMate.dao.service_type;

import com.example.JourneyMate.entity.service_type.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<HotelEntity, Integer> {

    List<HotelEntity> findByEstrellas(Integer estrellas);
}
