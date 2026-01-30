package com.example.JourneyMate.service.route;

import com.example.JourneyMate.entity.route.RutaEntity;

import java.util.List;

public interface RutaService {
    List<RutaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<RutaEntity> findAll();

    RutaEntity findById(Integer idRuta);

    RutaEntity saveRuta(RutaEntity ruta);

    void deleteBy(Integer idRuta);


}
