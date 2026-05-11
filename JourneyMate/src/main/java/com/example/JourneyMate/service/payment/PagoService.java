package com.example.JourneyMate.service.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;

import java.util.List;

/**
 * Servicio para la gestión de pagos.
 * Proporciona operaciones de consulta, filtrado y mantenimiento sobre {@link PagoEntity}.
 */
public interface PagoService {

    /**
     * Obtiene los pagos asociados a una reserva.
     *
     * @param idReserva identificador de la reserva
     * @return lista de pagos de la reserva
     */
    List<PagoEntity> findByReservaIdReserva(Integer idReserva);

    /**
     * Busca pagos por identificador.
     *
     * @param idPago identificador del pago
     * @return lista de pagos encontrados
     */
    List<PagoEntity> findById(Integer idPago);

    /**
     * Busca un pago por su identificador dentro de una reserva específica.
     *
     * @param idPago identificador del pago
     * @param idReserva identificador de la reserva
     * @return el pago encontrado o null si no existe
     */
    PagoEntity findById(Integer idPago, Integer idReserva);

    /**
     * Obtiene los pagos asociados a un hotel.
     *
     * @param idHotel identificador del hotel
     * @return lista de pagos del hotel
     */
    List<PagoEntity> findByHotelId(Integer idHotel);

    /**
     * Obtiene los pagos filtrados por hotel, reserva y servicio.
     *
     * @param idHotel identificador del hotel
     * @param idReserva identificador de la reserva
     * @param idServicio identificador del servicio
     * @return lista de pagos filtrados
     */
    List<PagoEntity> findByHotelId(Integer idHotel, Integer idReserva, Integer idServicio);

    /**
     * Guarda o actualiza un pago.
     *
     * @param pagoEntity entidad de pago
     * @return pago persistido
     */
    PagoEntity save(PagoEntity pagoEntity);

    /**
     * Elimina todos los pagos asociados a un hotel.
     *
     * @param idHotel identificador del hotel
     */
    void deleteByHotelId(Integer idHotel);
}