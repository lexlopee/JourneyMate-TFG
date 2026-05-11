package com.example.JourneyMate.service.recommendation;

import com.example.JourneyMate.entity.recommendation.TipoElementoEntity;

import java.util.List;

/**
 * Servicio para la gestión de tipos de elemento.
 * Define operaciones CRUD básicas sobre {@link TipoElementoEntity}.
 */
public interface TipoElementoService {

    /**
     * Obtiene todos los tipos de elemento registrados.
     *
     * @return lista de tipos de elemento
     */
    List<TipoElementoEntity> findAll();

    /**
     * Busca un tipo de elemento por su identificador.
     *
     * @param idTipoElemento identificador del tipo de elemento
     * @return el tipo de elemento encontrado o null si no existe
     */
    TipoElementoEntity findById(Integer idTipoElemento);

    /**
     * Guarda o actualiza un tipo de elemento.
     *
     * @param tipoElemento entidad de tipo de elemento
     * @return entidad persistida
     */
    TipoElementoEntity save(TipoElementoEntity tipoElemento);

    /**
     * Elimina un tipo de elemento por su identificador.
     *
     * @param idTipoElemento identificador del tipo de elemento a eliminar
     */
    void deleteById(Integer idTipoElemento);
}