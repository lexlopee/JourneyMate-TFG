package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.TrenRepository;
import com.example.JourneyMate.entity.service_type.TrenEntity;
import com.example.JourneyMate.service.service_type.TrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de trenes.
 * Proporciona operaciones CRUD sobre {@link TrenEntity}.
 */
@Service
public class TrenServiceImpl implements TrenService {

    @Autowired
    private TrenRepository trenRepository;

    /**
     * Obtiene todos los trenes registrados.
     *
     * @return lista de trenes
     */
    @Override
    public List<TrenEntity> findAll() {
        return trenRepository.findAll();
    }

    /**
     * Busca un tren por su identificador.
     *
     * @param idTren identificador del tren
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public TrenEntity findById(Integer idTren) {
        return trenRepository.findById(idTren).orElse(null);
    }

    /**
     * Guarda o actualiza un tren.
     *
     * @param trenEntity entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public TrenEntity save(TrenEntity trenEntity) {
        return trenRepository.save(trenEntity);
    }

    /**
     * Elimina un tren por su identificador.
     *
     * @param idTren identificador del tren a eliminar
     */
    @Override
    public void deleteById(Integer idTren) {
        trenRepository.deleteById(idTren);
    }
}