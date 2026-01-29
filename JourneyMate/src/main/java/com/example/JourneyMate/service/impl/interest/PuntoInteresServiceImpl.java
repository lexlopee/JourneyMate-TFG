package com.example.JourneyMate.service.impl.interest;

import com.example.JourneyMate.dao.interest.PuntoInteresRepository;
import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import com.example.JourneyMate.service.interest.PuntoInteresService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuntoInteresServiceImpl implements PuntoInteresService {

    private final PuntoInteresRepository puntoInteresRepository;

    public PuntoInteresServiceImpl(PuntoInteresRepository puntoInteresRepository) {
        this.puntoInteresRepository = puntoInteresRepository;
    }

    @Override
    public List<PuntoInteresEntity> findByCiudadIgnoreCase(String ciudad) {
        return puntoInteresRepository.findByCiudadIgnoreCase(ciudad);
    }

    @Override
    public List<PuntoInteresEntity> findByCategoriaIdCategoria(Integer idCategoria) {
        return puntoInteresRepository.findByCategoriaIdCategoria(idCategoria);
    }

    @Override
    public List<PuntoInteresEntity> findByAll() {
        return puntoInteresRepository.findAll();
    }

    @Override
    public PuntoInteresEntity findById(Integer idInteres) {
        return puntoInteresRepository.findById(idInteres).orElse(null);
    }

    @Override
    public PuntoInteresEntity save(PuntoInteresEntity puntoInteres) {
        return puntoInteresRepository.save(puntoInteres);
    }

    @Override
    public void deleteById(Integer idInteres) {
        puntoInteresRepository.deleteById(idInteres);
    }
}
