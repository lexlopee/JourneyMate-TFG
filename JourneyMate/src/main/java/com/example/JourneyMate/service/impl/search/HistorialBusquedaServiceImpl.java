package com.example.JourneyMate.service.impl.search;

import com.example.JourneyMate.dao.search.HistorialBusquedaRepository;
import com.example.JourneyMate.entity.search.HistorialBusquedaEntity;
import com.example.JourneyMate.service.search.HistorialBusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión del historial de búsquedas.
 * Permite realizar operaciones CRUD y consultas sobre el historial de búsquedas de los usuarios.
 */
@Service
public class HistorialBusquedaServiceImpl implements HistorialBusquedaService {

    @Autowired
    private HistorialBusquedaRepository historialBusquedaRepository;

    /**
     * Obtiene el historial de búsquedas asociado a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de búsquedas realizadas por el usuario
     */
    @Override
    public List<HistorialBusquedaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return historialBusquedaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    /**
     * Obtiene todo el historial de búsquedas registrado.
     *
     * @return lista completa del historial de búsquedas
     */
    @Override
    public List<HistorialBusquedaEntity> findAll() {
        return historialBusquedaRepository.findAll();
    }

    /**
     * Busca una entrada del historial de búsquedas por su identificador.
     *
     * @param idHistorial identificador del registro de historial
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public HistorialBusquedaEntity findById(Integer idHistorial) {
        return historialBusquedaRepository.findById(idHistorial).orElse(null);
    }

    /**
     * Guarda o actualiza una entrada en el historial de búsquedas.
     *
     * @param historial entidad de historial a persistir
     * @return la entidad guardada
     */
    @Override
    public HistorialBusquedaEntity save(HistorialBusquedaEntity historial) {
        return historialBusquedaRepository.save(historial);
    }

    /**
     * Elimina una entrada del historial de búsquedas por su identificador.
     *
     * @param idHistorial identificador del registro a eliminar
     */
    @Override
    public void deleteById(Integer idHistorial) {
        historialBusquedaRepository.deleteById(idHistorial);
    }
}