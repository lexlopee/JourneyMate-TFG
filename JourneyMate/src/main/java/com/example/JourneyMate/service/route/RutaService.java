package com.example.JourneyMate.service.route;

import com.example.JourneyMate.entity.route.RutaEntity;

import java.util.List;

public interface RutaService {
    List<RutaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<RutaEntity> findByAll();

    RutaEntity findByUserId(Integer idUser);

    RutaEntity saveRuta(RutaEntity ruta);

    void deleteByIdRuta(RutaEntity ruta);


}
