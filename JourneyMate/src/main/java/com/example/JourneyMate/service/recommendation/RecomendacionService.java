package com.example.JourneyMate.service.recommendation;

import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;

import java.util.List;

/**
 * Servicio para la gestión de recomendaciones.
 * Define operaciones de consulta, persistencia y eliminación sobre {@link RecomendacionEntity}.
 */
public interface RecomendacionService {

    /**
     * Obtiene las recomendaciones asociadas a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de recomendaciones del usuario
     */
    List<RecomendacionEntity> findByIdUsuario(Integer idUsuario);

    /**
     * Obtiene todas las recomendaciones registradas.
     *
     * @return lista de recomendaciones
     */
    List<RecomendacionEntity> findAll();

    /**
     * Busca una recomendación por su identificador.
     *
     * @param idRecomendacion identificador de la recomendación
     * @return la recomendación encontrada o null si no existe
     */
    RecomendacionEntity findById(Integer idRecomendacion);

    /**
     * Guarda o actualiza una recomendación.
     *
     * @param recomendacion entidad de recomendación
     * @return recomendación persistida
     */
    RecomendacionEntity save(RecomendacionEntity recomendacion);

    /**
     * Elimina una recomendación por su identificador.
     *
     * @param idRecomendacion identificador de la recomendación a eliminar
     */
    void deleteById(Integer idRecomendacion);
}