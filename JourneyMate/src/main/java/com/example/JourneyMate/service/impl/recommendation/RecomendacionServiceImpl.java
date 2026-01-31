package com.example.JourneyMate.service.impl.recommendation;

import com.example.JourneyMate.dao.recommendation.RecomendacionRepository;
import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;
import com.example.JourneyMate.service.recommendation.RecomendacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecomendacionServiceImpl implements RecomendacionService {

    @Autowired
    private RecomendacionRepository recommendacionRepository;

    @Override
    public List<RecomendacionEntity> findByIdUsuario(Integer idUsuario) {
        return recommendacionRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public List<RecomendacionEntity> findAll() {
        return recommendacionRepository.findAll();
    }

    @Override
    public RecomendacionEntity findById(Integer idRecomendacion) {
        return recommendacionRepository.findById(idRecomendacion).orElse(null);
    }

    @Override
    public RecomendacionEntity save(RecomendacionEntity recomendacion) {
        return recommendacionRepository.save(recomendacion);
    }

    @Override
    public void deleteById(Integer idRecomendacion) {
        recommendacionRepository.deleteById(idRecomendacion);
    }
}
