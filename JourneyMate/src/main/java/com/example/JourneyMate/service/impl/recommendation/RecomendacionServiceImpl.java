package com.example.JourneyMate.service.impl.recommendation;

import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;
import com.example.JourneyMate.service.recommendation.RecomendacionService;

import java.util.List;

public class RecomendacionServiceImpl implements RecomendacionService {
    @Override
    public List<RecomendacionEntity> findByIdUsuario(Integer idUsuario) {
        return List.of();
    }

    @Override
    public List<RecomendacionEntity> findByAll() {
        return List.of();
    }

    @Override
    public RecomendacionEntity findById(Integer idRecomendacion) {
        return null;
    }

    @Override
    public RecomendacionEntity save(RecomendacionEntity recomendacion) {
        return null;
    }

    @Override
    public void deleteById(Integer idRecomendacion) {

    }
}
