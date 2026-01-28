package com.example.JourneyMate.service.impl.interest;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import com.example.JourneyMate.service.interest.PuntoInteresService;

import java.util.List;

public class PuntoInteresServiceImpl implements PuntoInteresService {
    @Override
    public List<PuntoInteresEntity> findByCiudadIgnoreCase(String ciudad) {
        return List.of();
    }

    @Override
    public List<PuntoInteresEntity> findByCategoriaIdCategoria(Integer idCategoria) {
        return List.of();
    }

    @Override
    public List<PuntoInteresEntity> findByAll() {
        return List.of();
    }

    @Override
    public PuntoInteresEntity findById(Integer idInteres) {
        return null;
    }

    @Override
    public PuntoInteresEntity save(PuntoInteresEntity puntoInteres) {
        return null;
    }

    @Override
    public void deleteById(Integer idInteres) {

    }
}
