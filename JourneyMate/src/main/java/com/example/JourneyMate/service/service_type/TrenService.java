package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.TrenEntity;

import java.util.List;

/**
 * Servicio para la gestión de trenes.
 * Define operaciones CRUD básicas sobre {@link TrenEntity}.
 */
public interface TrenService {

    /**
     * Obtiene todos los trenes registrados.
     *
     * @return lista de trenes
     */
    List<TrenEntity> findAll();

    /**
     * Busca un tren por su identificador.
     *
     * @param idTren identificador del tren
     * @return el tren encontrado o null si no existe
     */
    TrenEntity findById(Integer idTren);

    /**
     * Guarda o actualiza un tren.
     *
     * @param trenEntity entidad de tren
     * @return tren persistido
     */
    TrenEntity save(TrenEntity trenEntity);

    /**
     * Elimina un tren por su identificador.
     *
     * @param idTren identificador del tren a eliminar
     */
    void deleteById(Integer idTren);
}