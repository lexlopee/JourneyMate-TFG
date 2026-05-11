package com.example.JourneyMate.service.impl.recommendation;

import com.example.JourneyMate.dao.recommendation.TipoElementoRepository;
import com.example.JourneyMate.entity.recommendation.TipoElementoEntity;
import com.example.JourneyMate.service.recommendation.TipoElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de tipos de elemento.
 * Proporciona operaciones CRUD básicas sobre {@link TipoElementoEntity}.
 */
@Service
public class TipoElementoServiceImpl implements TipoElementoService {

    @Autowired
    private TipoElementoRepository tipoElementoRepository;

    /**
     * Obtiene todos los tipos de elemento registrados.
     *
     * @return lista de todos los tipos de elemento
     */
    @Override
    public List<TipoElementoEntity> findAll() {
        return tipoElementoRepository.findAll();
    }

    /**
     * Busca un tipo de elemento por su identificador.
     *
     * @param idTipoElemento identificador del tipo de elemento
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public TipoElementoEntity findById(Integer idTipoElemento) {
        return tipoElementoRepository.findById(idTipoElemento).orElse(null);
    }

    /**
     * Guarda o actualiza un tipo de elemento.
     *
     * @param tipoElemento entidad a guardar
     * @return la entidad persistida
     */
    @Override
    public TipoElementoEntity save(TipoElementoEntity tipoElemento) {
        return tipoElementoRepository.save(tipoElemento);
    }

    /**
     * Elimina un tipo de elemento por su identificador.
     *
     * @param idTipoElemento identificador del tipo de elemento a eliminar
     */
    @Override
    public void deleteById(Integer idTipoElemento) {
        tipoElementoRepository.deleteById(idTipoElemento);
    }
}