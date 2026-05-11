package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.entity.booking.TipoReservaEntity;

import java.util.List;

/**
 * Servicio que define las operaciones de negocio relacionadas
 * con los tipos de reserva del sistema.
 *
 * Permite consultar, crear y eliminar tipos de reserva.
 */
public interface TipoReservaService {

    /**
     * Obtiene todos los tipos de reserva disponibles.
     *
     * @return lista de {@link TipoReservaEntity}
     */
    List<TipoReservaEntity> findAll();

    /**
     * Busca un tipo de reserva por su identificador.
     *
     * @param idTipoReserva identificador del tipo de reserva
     * @return entidad {@link TipoReservaEntity} correspondiente
     */
    TipoReservaEntity findById(Integer idTipoReserva);

    /**
     * Guarda un tipo de reserva en el sistema.
     *
     * Puede utilizarse tanto para crear como para actualizar.
     *
     * @param tipoReserva entidad del tipo de reserva
     * @return tipo de reserva guardado
     */
    TipoReservaEntity save(TipoReservaEntity tipoReserva);

    /**
     * Elimina un tipo de reserva por su identificador.
     *
     * @param idTipoReserva identificador del tipo de reserva a eliminar
     */
    void deleteById(Integer idTipoReserva);
}