package com.example.JourneyMate.service.impl.route;

import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;
import com.example.JourneyMate.service.route.RutaPuntoInteresService;

import java.util.List;

public class RutaPuntoInteresServiceImpl implements RutaPuntoInteresService {
    @Override
    public List<RutaPuntoInteresEntity> findByIdRutaOrderByOrden(Integer idRuta) {
        return List.of();
    }

    @Override
    public List<RutaPuntoInteresEntity> findByAll() {
        return List.of();
    }

    @Override
    public RutaPuntoInteresEntity findById(Integer idRuta) {
        return null;
    }

    @Override
    public RutaPuntoInteresEntity save(RutaPuntoInteresEntity rutaInteres) {
        return null;
    }

    @Override
    public void deleteById(Integer idRutaInteres) {

    }
}
