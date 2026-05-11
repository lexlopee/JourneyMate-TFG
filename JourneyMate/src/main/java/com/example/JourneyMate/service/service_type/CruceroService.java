package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.CruceroEntity;

import java.util.List;

/**
 * Servicio para la gestión de cruceros.
 * Define operaciones CRUD básicas sobre {@link CruceroEntity}.
 */
public interface CruceroService {

    /**
     * Obtiene todos los cruceros registrados.
     *
     * @return lista de cruceros
     */
    List<CruceroEntity> findAll();

    /**
     * Busca un crucero por su identificador.
     *
     * @param idCrucero identificador del crucero
     * @return el crucero encontrado o null si no existe
     */
    CruceroEntity findByIdCrucero(Integer idCrucero);

    /**
     * Guarda o actualiza un crucero.
     *
     * @param crucero entidad de crucero
     * @return crucero persistido
     */
    CruceroEntity save(CruceroEntity crucero);

    /**
     * Elimina un crucero por su identificador.
     *
     * @param idCrucero identificador del crucero a eliminar
     */
    void deleteByIdCrucero(Integer idCrucero);
}