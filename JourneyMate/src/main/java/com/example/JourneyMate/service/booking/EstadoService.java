package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.entity.booking.EstadoEntity;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que define las operaciones de negocio relacionadas
 * con los estados de una reserva o entidad de booking.
 *
 * Proporciona métodos para consultar, crear, actualizar y eliminar estados,
 * así como utilidades de verificación y búsqueda por nombre.
 */
public interface EstadoService {

    /**
     * Obtiene todos los estados disponibles.
     *
     * @return lista de {@link EstadoEntity}
     */
    List<EstadoEntity> findAll();

    /**
     * Busca un estado por su identificador.
     *
     * @param idEstado identificador del estado
     * @return {@link Optional} con el estado si existe
     */
    Optional<EstadoEntity> findById(Integer idEstado);

    /**
     * Crea un nuevo estado.
     *
     * @param estado entidad del estado a crear
     * @return estado creado {@link EstadoEntity}
     */
    EstadoEntity crear(EstadoEntity estado);

    /**
     * Actualiza un estado existente.
     *
     * @param idEstado identificador del estado a actualizar
     * @param estado datos actualizados del estado
     * @return estado actualizado {@link EstadoEntity}
     */
    EstadoEntity actualizar(Integer idEstado, EstadoEntity estado);

    /**
     * Elimina un estado por su identificador.
     *
     * @param idEstado identificador del estado a eliminar
     */
    void deleteById(Integer idEstado);

    /**
     * Verifica si existe un estado por su identificador.
     *
     * @param idEstado identificador del estado
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Integer idEstado);

    /**
     * Busca un estado por su nombre.
     *
     * Muy útil en el contexto de reservas para obtener estados como
     * "PENDIENTE", "CONFIRMADA" o "CANCELADA".
     *
     * @param nombre nombre del estado
     * @return entidad {@link EstadoEntity} correspondiente
     */
    EstadoEntity findByNombre(String nombre);
}