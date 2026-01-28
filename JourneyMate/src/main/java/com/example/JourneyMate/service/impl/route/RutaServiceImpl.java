package com.example.JourneyMate.service.impl.route;

import com.example.JourneyMate.entity.route.RutaEntity;
import com.example.JourneyMate.service.route.RutaService;

import java.util.List;

public class RutaServiceImpl implements RutaService {
    @Override
    public List<RutaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return List.of();
    }

    @Override
    public List<RutaEntity> findByAll() {
        return List.of();
    }

    @Override
    public RutaEntity findByUserId(Integer idUser) {
        return null;
    }

    @Override
    public RutaEntity saveRuta(RutaEntity ruta) {
        return null;
    }

    @Override
    public void deleteByIdRuta(RutaEntity ruta) {

    }
}
