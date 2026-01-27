package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PuntoInteresRepository extends JpaRepository<PuntoInteresEntity, Integer> {

    List<PuntoInteresEntity> findByCiudadIgnoreCase(String ciudad);

    List<PuntoInteresEntity> findByCategoriaIdCategoria(Integer idCategoria);
}
