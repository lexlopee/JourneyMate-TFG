package com.example.JourneyMate.service.interest;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;

import java.util.List;

/**
 * Servicio para la gestión de puntos de interés.
 * Define operaciones de consulta, filtrado y mantenimiento sobre {@link PuntoInteresEntity}.
 */
public interface PuntoInteresService {

    /**
     * Busca puntos de interés por ciudad, ignorando mayúsculas y minúsculas.
     *
     * @param ciudad nombre de la ciudad
     * @return lista de puntos de interés en la ciudad indicada
     */
    List<PuntoInteresEntity> findByCiudadIgnoreCase(String ciudad);

    /**
     * Busca puntos de interés por identificador de categoría.
     *
     * @param idCategoria identificador de la categoría
     * @return lista de puntos de interés asociados a la categoría
     */
    List<PuntoInteresEntity> findByCategoriaIdCategoria(Integer idCategoria);

    /**
     * Obtiene todos los puntos de interés registrados.
     *
     * @return lista de puntos de interés
     */
    List<PuntoInteresEntity> findAll();

    /**
     * Busca un punto de interés por su identificador.
     *
     * @param idInteres identificador del punto de interés
     * @return la entidad encontrada o null si no existe
     */
    PuntoInteresEntity findById(Integer idInteres);

    /**
     * Guarda o actualiza un punto de interés.
     *
     * @param puntoInteres entidad a persistir
     * @return entidad guardada
     */
    PuntoInteresEntity save(PuntoInteresEntity puntoInteres);

    /**
     * Elimina un punto de interés por su identificador.
     *
     * @param idInteres identificador del punto de interés
     */
    void deleteById(Integer idInteres);
}