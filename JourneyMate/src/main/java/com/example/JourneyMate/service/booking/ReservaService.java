package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que define las operaciones de negocio relacionadas
 * con las reservas del sistema.
 *
 * Incluye creación completa de reservas, consultas por filtros,
 * y gestión del historial y estados.
 */
public interface ReservaService {

    /**
     * Obtiene todas las reservas registradas.
     *
     * @return lista de {@link ReservaEntity}
     */
    List<ReservaEntity> findAll();

    /**
     * Busca una reserva por su identificador.
     *
     * @param idReserva identificador de la reserva
     * @return {@link Optional} con la reserva si existe
     */
    Optional<ReservaEntity> findById(Integer idReserva);

    /**
     * Crea una reserva completa a partir de un DTO.
     *
     * Este método centraliza la lógica de creación de reservas,
     * incluyendo relaciones con usuario, servicio, estado, etc.
     *
     * @param dto datos necesarios para crear la reserva
     * @return reserva creada {@link ReservaEntity}
     */
    ReservaEntity crearCompleta(ReservaRequestDTO dto);

    /**
     * Actualiza una reserva existente.
     *
     * @param idReserva identificador de la reserva
     * @param reserva datos actualizados
     * @return reserva actualizada {@link ReservaEntity}
     */
    ReservaEntity actualizar(Integer idReserva, ReservaEntity reserva);

    /**
     * Elimina una reserva por su identificador.
     *
     * @param idReserva identificador de la reserva a eliminar
     */
    void deleteById(Integer idReserva);

    /**
     * Verifica si existe una reserva por su identificador.
     *
     * @param idReserva identificador de la reserva
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Integer idReserva);

    /**
     * Obtiene todas las reservas asociadas a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de reservas del usuario
     */
    List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    /**
     * Obtiene reservas filtradas por el nombre del estado.
     *
     * Ejemplo: PENDIENTE, CONFIRMADA, CANCELADA.
     *
     * @param estado nombre del estado
     * @return lista de reservas filtradas
     */
    List<ReservaEntity> findByEstadoNombre(String estado);

    /**
     * Obtiene reservas dentro de un rango de fechas.
     *
     * @param inicio fecha inicial
     * @param fin fecha final
     * @return lista de reservas en el rango indicado
     */
    List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);

    /**
     * Obtiene reservas de un usuario en formato DTO simplificado.
     *
     * @param idUsuario identificador del usuario
     * @return lista de {@link ReservaListDTO}
     */
    List<ReservaListDTO> findDTOsByUsuarioId(Integer idUsuario);

    /**
     * Obtiene el historial de reservas de un usuario en formato DTO.
     *
     * @param idUsuario identificador del usuario
     * @return lista de {@link ReservaListDTO}
     */
    List<ReservaListDTO> findHistorialByUsuarioId(Integer idUsuario);
}