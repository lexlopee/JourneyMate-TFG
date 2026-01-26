package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.service_type.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<HotelEntity,Integer> {
}
