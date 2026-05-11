package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.service.booking.EstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de estados de reserva.
 *
 * Proporciona operaciones CRUD para la entidad {@link EstadoEntity},
 * así como consultas auxiliares como búsqueda por nombre o verificación de existencia.
 */
@Service
@RequiredArgsConstructor
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;

    /**
     * Busca un estado por su nombre (ignorando mayúsculas/minúsculas).
     *
     * @param nombre nombre del estado
     * @return entidad encontrada o null si no existe
     */
    @Override
    public EstadoEntity findByNombre(String nombre) {
        return estadoRepository
                .findByNombreIgnoreCase(nombre)
                .orElse(null);
    }

    /**
     * Obtiene todos los estados disponibles.
     *
     * @return lista de estados
     */
    @Override
    public List<EstadoEntity> findAll() {
        return estadoRepository.findAll();
    }

    /**
     * Busca un estado por su identificador.
     *
     * @param idEstado identificador del estado
     * @return Optional con la entidad si existe
     */
    @Override
    public Optional<EstadoEntity> findById(Integer idEstado) {
        return estadoRepository.findById(idEstado);
    }

    /**
     * Crea un nuevo estado en la base de datos.
     *
     * @param estado entidad a guardar
     * @return estado persistido
     */
    @Override
    public EstadoEntity crear(EstadoEntity estado) {
        return estadoRepository.save(estado);
    }

    /**
     * Actualiza un estado existente.
     *
     * Si el estado existe, se sobrescriben sus datos con los nuevos valores.
     *
     * @param idEstado identificador del estado a actualizar
     * @param estado nuevos datos del estado
     * @return estado actualizado o null si no existe
     */
    @Override
    public EstadoEntity actualizar(Integer idEstado, EstadoEntity estado) {
        return estadoRepository.findById(idEstado)
                .map(e -> {
                    estado.setIdEstado(idEstado);
                    return estadoRepository.save(estado);
                })
                .orElse(null);
    }

    /**
     * Elimina un estado por su identificador.
     *
     * @param idEstado identificador del estado a eliminar
     */
    @Override
    public void deleteById(Integer idEstado) {
        estadoRepository.deleteById(idEstado);
    }

    /**
     * Verifica si un estado existe en la base de datos.
     *
     * @param idEstado identificador del estado
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsById(Integer idEstado) {
        return estadoRepository.existsById(idEstado);
    }
}