package com.example.JourneyMate.service.impl.route;

import com.example.JourneyMate.dao.route.RutaPuntoInteresRepository;
import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;
import com.example.JourneyMate.service.route.RutaPuntoInteresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de la relación entre rutas y puntos de interés.
 * Proporciona operaciones para consultar, guardar y eliminar asociaciones entre rutas y puntos.
 */
@Service
public class RutaPuntoInteresServiceImpl implements RutaPuntoInteresService {

    @Autowired
    private RutaPuntoInteresRepository rutaPuntoInteresRepository;

    /**
     * Obtiene los puntos de interés asociados a una ruta ordenados por el campo orden.
     *
     * @param idRuta identificador de la ruta
     * @return lista de puntos de interés asociados a la ruta ordenados
     */
    @Override
    public List<RutaPuntoInteresEntity> findByIdRutaOrderByOrden(Integer idRuta) {
        return rutaPuntoInteresRepository.findByRuta_IdRutaOrderByOrden(idRuta);
    }

    /**
     * Obtiene todas las relaciones entre rutas y puntos de interés.
     *
     * @return lista completa de relaciones ruta–punto de interés
     */
    @Override
    public List<RutaPuntoInteresEntity> findAll() {
        return rutaPuntoInteresRepository.findAll();
    }

    /**
     * Busca una relación ruta–punto de interés por su identificador.
     *
     * @param idRuta identificador de la relación
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public RutaPuntoInteresEntity findById(Integer idRuta) {
        return rutaPuntoInteresRepository.findById(idRuta).orElse(null);
    }

    /**
     * Guarda o actualiza una relación entre ruta y punto de interés.
     *
     * @param rutaInteres entidad a guardar
     * @return la entidad persistida
     */
    @Override
    public RutaPuntoInteresEntity save(RutaPuntoInteresEntity rutaInteres) {
        return rutaPuntoInteresRepository.save(rutaInteres);
    }

    /**
     * Elimina una relación entre ruta y punto de interés por su identificador.
     *
     * @param idRutaInteres identificador de la relación a eliminar
     */
    @Override
    public void deleteById(Integer idRutaInteres) {
        rutaPuntoInteresRepository.deleteById(idRutaInteres);
    }
}