package com.example.JourneyMate.service.impl.payment;

import com.example.JourneyMate.dao.payment.PagoRepository;
import com.example.JourneyMate.entity.payment.PagoEntity;
import com.example.JourneyMate.service.payment.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private final PagoRepository pagoRepository;

    public PagoServiceImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public List<PagoEntity> findByReservaIdReserva(Integer idReserva) {
        return pagoRepository.findByReserva_IdReserva(idReserva);
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
        return pagoRepository.findPagosByHotel(idHotel);
    }

    @Override
    public List<PagoEntity> findByHotelId(Integer idHotel, Integer idReserva, Integer idServicio) {
        return pagoRepository.findPagosHotelCompleto(idHotel, idReserva, idServicio);
    }

    @Override
    public PagoEntity save(PagoEntity pagoEntity) {
        return pagoRepository.save(pagoEntity);
    }

    @Override
    @Transactional
    public void deleteByHotelId(Integer idHotel) {
        pagoRepository.deletePagosByHotel(idHotel);
    }
}
