package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<ReservaEntity,Integer> {
}
