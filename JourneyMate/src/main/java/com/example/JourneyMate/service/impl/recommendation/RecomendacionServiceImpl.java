package com.example.JourneyMate.service.impl.recommendation;

import com.example.JourneyMate.dao.recommendation.RecomendacionRepository;
import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;
import com.example.JourneyMate.service.recommendation.RecomendacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de recomendaciones.
 * Proporciona operaciones CRUD y consultas relacionadas con las recomendaciones de usuarios.
 */
@Service
public class RecomendacionServiceImpl implements RecomendacionService {

    @Autowired
    private RecomendacionRepository recommendacionRepository;

    /**
     * Obtiene todas las recomendaciones asociadas a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de recomendaciones del usuario
     */
    @Override
    public List<RecomendacionEntity> findByIdUsuario(Integer idUsuario) {
        return recommendacionRepository.findByIdUsuario(idUsuario);
    }

    /**
     * Obtiene todas las recomendaciones almacenadas.
     *
     * @return lista completa de recomendaciones
     */
    @Override
    public List<RecomendacionEntity> findAll() {
        return recommendacionRepository.findAll();
    }

    /**
     * Busca una recomendación por su identificador.
     *
     * @param idRecomendacion identificador de la recomendación
     * @return la recomendación encontrada o null si no existe
     */
    @Override
    public RecomendacionEntity findById(Integer idRecomendacion) {
        return recommendacionRepository.findById(idRecomendacion).orElse(null);
    }

    /**
     * Guarda o actualiza una recomendación.
     *
     * @param recomendacion entidad de recomendación a guardar
     * @return la entidad guardada
     */
    @Override
    public RecomendacionEntity save(RecomendacionEntity recomendacion) {
        return recommendacionRepository.save(recomendacion);
    }

    /**
     * Elimina una recomendación por su identificador.
     *
     * @param idRecomendacion identificador de la recomendación a eliminar
     */
    @Override
    public void deleteById(Integer idRecomendacion) {
        recommendacionRepository.deleteById(idRecomendacion);
    }
}