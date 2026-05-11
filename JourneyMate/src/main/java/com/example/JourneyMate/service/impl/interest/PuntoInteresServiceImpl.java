package com.example.JourneyMate.service.impl.interest;

import com.example.JourneyMate.dao.interest.PuntoInteresRepository;
import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import com.example.JourneyMate.service.interest.PuntoInteresService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de gestión de puntos de interés.
 * Permite consultar, crear, actualizar y eliminar puntos turísticos
 * asociados a ciudades y categorías.
 */
@Service
public class PuntoInteresServiceImpl implements PuntoInteresService {

    private final PuntoInteresRepository puntoInteresRepository;

    /**
     * Constructor con inyección del repositorio de puntos de interés.
     */
    public PuntoInteresServiceImpl(PuntoInteresRepository puntoInteresRepository) {
        this.puntoInteresRepository = puntoInteresRepository;
    }

    /**
     * Obtiene los puntos de interés filtrados por ciudad (ignorando mayúsculas/minúsculas).
     */
    @Override
    public List<PuntoInteresEntity> findByCiudadIgnoreCase(String ciudad) {
        return puntoInteresRepository.findByCiudadIgnoreCase(ciudad);
    }

    /**
     * Obtiene los puntos de interés filtrados por categoría.
     */
    @Override
    public List<PuntoInteresEntity> findByCategoriaIdCategoria(Integer idCategoria) {
        return puntoInteresRepository.findByCategoriaIdCategoria(idCategoria);
    }

    /**
     * Obtiene todos los puntos de interés registrados.
     */
    @Override
    public List<PuntoInteresEntity> findAll() {
        return puntoInteresRepository.findAll();
    }

    /**
     * Busca un punto de interés por su ID.
     */
    @Override
    public PuntoInteresEntity findById(Integer idInteres) {
        return puntoInteresRepository.findById(idInteres).orElse(null);
    }

    /**
     * Guarda o actualiza un punto de interés en la base de datos.
     */
    @Override
    public PuntoInteresEntity save(PuntoInteresEntity puntoInteres) {
        return puntoInteresRepository.save(puntoInteres);
    }

    /**
     * Elimina un punto de interés por su identificador.
     */
    @Override
    public void deleteById(Integer idInteres) {
        puntoInteresRepository.deleteById(idInteres);
    }
}