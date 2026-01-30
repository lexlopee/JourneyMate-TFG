package com.example.JourneyMate.service.impl.route;

import com.example.JourneyMate.dao.route.RutaPuntoInteresRepository;
import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;
import com.example.JourneyMate.service.route.RutaPuntoInteresService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RutaPuntoInteresServiceImpl implements RutaPuntoInteresService {

    @Autowired
    private RutaPuntoInteresRepository rutaPuntoInteresRepository;

    @Override
    public List<RutaPuntoInteresEntity> findByIdRutaOrderByOrden(Integer idRuta) {
        return rutaPuntoInteresRepository.findByRuta_IdRutaOrderByOrden(idRuta);
    }

    @Override
    public List<RutaPuntoInteresEntity> findAll() {
        return rutaPuntoInteresRepository.findAll();
    }

    @Override
    public RutaPuntoInteresEntity findById(Integer idRuta) {
        return rutaPuntoInteresRepository.findById(idRuta).orElse(null);
    }

    @Override
    public RutaPuntoInteresEntity save(RutaPuntoInteresEntity rutaInteres) {
        return rutaPuntoInteresRepository.save(rutaInteres);
    }

    @Override
    public void deleteById(Integer idRutaInteres) {
        rutaPuntoInteresRepository.deleteById(idRutaInteres);
    }
}
