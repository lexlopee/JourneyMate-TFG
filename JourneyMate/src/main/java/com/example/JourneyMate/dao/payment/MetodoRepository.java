package com.example.JourneyMate.dao.payment;

import com.example.JourneyMate.entity.payment.MetodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoRepository extends JpaRepository<MetodoEntity, Integer> {
    java.util.Optional<MetodoEntity> findByNombre(String nombre);
}
