package com.example.JourneyMate.service.impl.payment;

import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.payment.PagoService;

import java.util.List;

public class PagoServiceImpl implements PagoService {
    @Override
    public List<PagoEntity> findByReservaIdReserva(Integer idReserva) {
        return List.of();
    }

    @Override
    public List<PagoEntity> findById(Integer idPago) {
        return List.of();
    }

    @Override
    public PagoEntity findById(Integer idPago, Integer idReserva) {
        return null;
    }

    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel) {
        return List.of();
    }

    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel, Integer idReserva, Integer idServicio) {
        return List.of();
    }

    @Override
    public PagoEntity save(PagoEntity pagoEntity) {
        return null;
    }

    @Override
    public void deleteByHotelId(Integer idHotel) {

    }
}
