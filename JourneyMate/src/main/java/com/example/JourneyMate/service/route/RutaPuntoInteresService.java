package com.example.JourneyMate.service.route;

import com.example.JourneyMate.entity.route.RutaPuntoInteresEntity;

import java.util.List;

/**
 * Servicio para la gestión de la relación entre rutas y puntos de interés.
 * Define operaciones de consulta, ordenación y mantenimiento de {@link RutaPuntoInteresEntity}.
 */
public interface RutaPuntoInteresService {

    /**
     * Obtiene los puntos de interés asociados a una ruta ordenados por su posición.
     *
     * @param idRuta identificador de la ruta
     * @return lista de puntos de interés ordenados
     */
    List<RutaPuntoInteresEntity> findByIdRutaOrderByOrden(Integer idRuta);

    /**
     * Obtiene todas las relaciones entre rutas y puntos de interés.
     *
     * @return lista completa de relaciones ruta–punto de interés
     */
    List<RutaPuntoInteresEntity> findAll();

    /**
     * Busca una relación ruta–punto de interés por su identificador.
     *
     * @param idRuta identificador de la relación
     * @return la entidad encontrada o null si no existe
     */
    RutaPuntoInteresEntity findById(Integer idRuta);

    /**
     * Guarda o actualiza una relación entre ruta y punto de interés.
     *
     * @param rutaInteres entidad a persistir
     * @return entidad guardada
     */
    RutaPuntoInteresEntity save(RutaPuntoInteresEntity rutaInteres);

    /**
     * Elimina una relación entre ruta y punto de interés por su identificador.
     *
     * @param idRutaInteres identificador de la relación a eliminar
     */
    void deleteById(Integer idRutaInteres);
}