package com.example.JourneyMate.dao.recommendation;

import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecomendacionRepository extends JpaRepository<RecomendacionEntity, Integer> {
    List<RecomendacionEntity> findByIdUsuario(Integer idUsuario);
}
