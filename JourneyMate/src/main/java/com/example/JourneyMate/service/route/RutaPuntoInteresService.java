package com.example.JourneyMate.service.route;

import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;

import java.util.List;

public interface RutaPuntoInteresService {
    List<RutaPuntoInteresEntity> findByIdRutaOrderByOrden(Integer idRuta);

    List<RutaPuntoInteresEntity> findAll();

    RutaPuntoInteresEntity findById(Integer idRuta);

    RutaPuntoInteresEntity save(RutaPuntoInteresEntity rutaInteres);

    void deleteById(Integer idRutaInteres);

}
