package com.example.JourneyMate.service.impl.payment;

import com.example.JourneyMate.dao.payment.PagoRepository;
import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.payment.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Override
    public List<PagoEntity> findByReservaIdReserva(Integer idReserva) {
        return pagoRepository.findByReservaIdReserva(idReserva);
    }

    @Override
    public List<PagoEntity> findById(Integer idPago) {
        return pagoRepository.findByIdPago(idPago);
    }

    @Override
    public PagoEntity findById(Integer idPago, Integer idReserva) {
        return pagoRepository.findByIdPagoAndReserva_IdReserva(idPago, idReserva);
    }

    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel) {
        return pagoRepository.findByReserva_Hotel_IdHotel(idHotel);
    }

    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel, Integer idReserva, Integer idServicio) {
        return pagoRepository.findByReserva_Hotel_IdHotelAndReserva_IdReservaAndReserva_Servicio_IdServicio(idHotel, idReserva, idServicio);
    }

    @Override
    public PagoEntity save(PagoEntity pagoEntity) {
        return pagoRepository.save(pagoEntity);
    }

    @Override
    public void deleteByHotelId(Integer idHotel) {
        pagoRepository.deleteByReserva_Hotel_IdHotel(idHotel);
    }
}
