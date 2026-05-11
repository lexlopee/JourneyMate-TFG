package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.CocheEntity;

import java.util.List;

/**
 * Servicio para la gestión de coches.
 * Define operaciones CRUD básicas sobre {@link CocheEntity}.
 */
public interface CocheService {

    /**
     * Obtiene todos los coches registrados.
     *
     * @return lista de coches
     */
    List<CocheEntity> findAll();

    /**
     * Busca un coche por su identificador.
     *
     * @param idVTC identificador del coche
     * @return el coche encontrado o null si no existe
     */
    CocheEntity findById(Integer idVTC);

    /**
     * Guarda o actualiza un coche.
     *
     * @param cocheEntity entidad de coche
     * @return coche persistido
     */
    CocheEntity save(CocheEntity cocheEntity);

    /**
     * Elimina un coche por su identificador.
     *
     * @param idVTC identificador del coche a eliminar
     */
    void deleteById(Integer idVTC);
}