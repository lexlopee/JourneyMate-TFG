package com.example.JourneyMate.service.interest;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;

import java.util.List;

public interface PuntoInteresService {

    List<PuntoInteresEntity> findByCiudadIgnoreCase(String ciudad);

    List<PuntoInteresEntity> findByCategoriaIdCategoria(Integer idCategoria);

    List<PuntoInteresEntity> findByAll();

    PuntoInteresEntity findById(Integer idInteres);

    PuntoInteresEntity save(PuntoInteresEntity puntoInteres);

    void deleteById(Integer idInteres);
}
