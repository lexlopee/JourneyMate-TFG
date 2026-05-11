package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.CocheRepository;
import com.example.JourneyMate.entity.service_type.CocheEntity;
import com.example.JourneyMate.service.service_type.CocheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de coches.
 * Proporciona operaciones CRUD sobre {@link CocheEntity}.
 */
@Service
public class CocheServiceImpl implements CocheService {

    @Autowired
    private CocheRepository cocheRepository;

    /**
     * Obtiene todos los coches registrados.
     *
     * @return lista de coches
     */
    @Override
    public List<CocheEntity> findAll() {
        return cocheRepository.findAll();
    }

    /**
     * Busca un coche por su identificador.
     *
     * @param idVTC identificador del coche
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public CocheEntity findById(Integer idVTC) {
        return cocheRepository.findById(idVTC).orElse(null);
    }

    /**
     * Guarda o actualiza un coche.
     *
     * @param cocheEntity entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public CocheEntity save(CocheEntity cocheEntity) {
        return cocheRepository.save(cocheEntity);
    }

    /**
     * Elimina un coche por su identificador.
     *
     * @param idVTC identificador del coche a eliminar
     */
    @Override
    public void deleteById(Integer idVTC) {
        cocheRepository.deleteById(idVTC);
    }
}