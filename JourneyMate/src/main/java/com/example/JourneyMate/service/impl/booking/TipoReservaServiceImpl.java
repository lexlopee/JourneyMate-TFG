package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.TipoReservaRepository;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.service.booking.TipoReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoReservaServiceImpl implements TipoReservaService {

    @Autowired
    private TipoReservaRepository tipoReservaRepository;

    @Override
    public List<TipoReservaEntity> findAll() {
        return tipoReservaRepository.findAll();
    }

    @Override
    public TipoReservaEntity findById(Integer idTipoReserva) {
        return tipoReservaRepository.findById(idTipoReserva).orElse(null);
    }

    @Override
    public TipoReservaEntity save(TipoReservaEntity tipoReserva) {
        return tipoReservaRepository.save(tipoReserva);
    }

    @Override
    public void deleteById(Integer idTipoReserva) {
        tipoReservaRepository.deleteById(idTipoReserva);
    }
}
