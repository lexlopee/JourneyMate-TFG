package com.example.JourneyMate.service.impl.payment;

import com.example.JourneyMate.dao.payment.PagoRepository;
import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.payment.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de pagos.
 * Encargado de la lógica de consulta, creación y eliminación de pagos
 * asociados a reservas y servicios turísticos.
 */
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    /**
     * Obtiene todos los pagos asociados a una reserva.
     */
    @Override
    public List<PagoEntity> findByReservaIdReserva(Integer idReserva) {
        return pagoRepository.findByReserva_IdReserva(idReserva);
    }

    /**
     * Busca pagos por su identificador.
     */
    @Override
    public List<PagoEntity> findById(Integer idPago) {
        return pagoRepository.findByIdPago(idPago);
    }

    /**
     * Busca un pago específico por ID de pago y reserva.
     */
    @Override
    public PagoEntity findById(Integer idPago, Integer idReserva) {
        return pagoRepository.findByIdPagoAndReserva_IdReserva(idPago, idReserva);
    }

    /**
     * Obtiene todos los pagos asociados a un hotel.
     */
    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel) {
        return pagoRepository.findPagosByHotel(idHotel);
    }

    /**
     * Obtiene pagos filtrados por hotel, reserva y servicio.
     */
    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel, Integer idReserva, Integer idServicio) {
        return pagoRepository.findPagosHotelCompleto(idHotel, idReserva, idServicio);
    }

    /**
     * Guarda o actualiza un pago en la base de datos.
     */
    @Override
    public PagoEntity save(PagoEntity pagoEntity) {
        return pagoRepository.save(pagoEntity);
    }

    /**
     * Elimina todos los pagos asociados a un hotel.
     * Operación transaccional para asegurar consistencia de datos.
     */
    @Override
    @Transactional
    public void deleteByHotelId(Integer idHotel) {
        pagoRepository.deletePagosByHotel(idHotel);
    }
}