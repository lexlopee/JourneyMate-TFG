package com.example.JourneyMate.dao.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<PagoEntity, Integer> {
    List<PagoEntity> findByReservaIdReserva(Integer idReserva);

}
