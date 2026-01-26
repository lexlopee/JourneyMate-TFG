package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.payment.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<PagoEntity,Integer> {
}
