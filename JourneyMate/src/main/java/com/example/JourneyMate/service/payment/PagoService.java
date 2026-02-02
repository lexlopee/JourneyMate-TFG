package com.example.JourneyMate.service.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;

import java.util.List;

public interface PagoService {

    List<PagoEntity> findByReservaIdReserva(Integer idReserva);

    List<PagoEntity> findById(Integer idPago);

    PagoEntity findById(Integer idPago, Integer idReserva);

    List<PagoEntity> findByHotelId(Integer idHotel);

    List<PagoEntity> findByHotelId(Integer idHotel, Integer idReserva, Integer idServicio);

    PagoEntity save(PagoEntity pagoEntity);

    void deleteByHotelId(Integer idHotel);
}
