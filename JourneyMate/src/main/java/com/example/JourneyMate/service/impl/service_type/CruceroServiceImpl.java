package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.CruceroRepository;
import com.example.JourneyMate.entity.service_type.CruceroEntity;
import com.example.JourneyMate.service.service_type.CruceroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de cruceros.
 * Proporciona operaciones CRUD sobre {@link CruceroEntity}.
 */
@Service
public class CruceroServiceImpl implements CruceroService {

    @Autowired
    private CruceroRepository cruceroRepository;

    /**
     * Obtiene todos los cruceros registrados.
     *
     * @return lista de cruceros
     */
    @Override
    public List<CruceroEntity> findAll() {
        return cruceroRepository.findAll();
    }

    /**
     * Busca un crucero por su identificador.
     *
     * @param idCrucero identificador del crucero
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public CruceroEntity findByIdCrucero(Integer idCrucero) {
        return cruceroRepository.findById(idCrucero).orElse(null);
    }

    /**
     * Guarda o actualiza un crucero.
     *
     * @param crucero entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public CruceroEntity save(CruceroEntity crucero) {
        return cruceroRepository.save(crucero);
    }

    /**
     * Elimina un crucero por su identificador.
     *
     * @param idCrucero identificador del crucero a eliminar
     */
    @Override
    public void deleteByIdCrucero(Integer idCrucero) {
        cruceroRepository.deleteById(idCrucero);
    }
}