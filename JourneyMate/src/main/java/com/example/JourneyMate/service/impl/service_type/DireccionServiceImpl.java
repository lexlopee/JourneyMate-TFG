package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.DireccionRepository;
import com.example.JourneyMate.entity.service_type.DireccionEntity;
import com.example.JourneyMate.service.service_type.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de direcciones.
 * Proporciona operaciones CRUD sobre {@link DireccionEntity}.
 */
@Service
public class DireccionServiceImpl implements DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    /**
     * Obtiene todas las direcciones registradas.
     *
     * @return lista de direcciones
     */
    @Override
    public List<DireccionEntity> findAll() {
        return direccionRepository.findAll();
    }

    /**
     * Busca una dirección por su identificador.
     *
     * @param idDireccion identificador de la dirección
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public DireccionEntity findById(Integer idDireccion) {
        return direccionRepository.findById(idDireccion).orElse(null);
    }

    /**
     * Guarda o actualiza una dirección.
     *
     * @param direccion entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public DireccionEntity save(DireccionEntity direccion) {
        return direccionRepository.save(direccion);
    }

    /**
     * Elimina una dirección por su identificador.
     *
     * @param idDireccion identificador de la dirección a eliminar
     */
    @Override
    public void deleteById(Integer idDireccion) {
        direccionRepository.deleteById(idDireccion);
    }
}