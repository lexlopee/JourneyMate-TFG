package com.example.JourneyMate.service.booking;

import com.example.JourneyMate.entity.booking.TipoReservaEntity;

import java.util.List;

public interface TipoReservaService {
    List<TipoReservaEntity> findAll();

    TipoReservaEntity findById(Integer idTipoReserva);

    TipoReservaEntity save(TipoReservaEntity tipoReserva);

    void deleteById(Integer idTipoReserva);

}
