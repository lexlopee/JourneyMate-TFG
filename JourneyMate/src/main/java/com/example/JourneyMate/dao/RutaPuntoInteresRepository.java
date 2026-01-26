package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutaPuntoInteresRepository extends JpaRepository<RutaPuntoInteresEntity,Integer> {
    List<RutaPuntoInteresEntity> findByIdRutaOrderByOrden(Integer idRuta);
}
