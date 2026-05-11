package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.TipoReservaRepository;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.service.booking.TipoReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de gestión de tipos de reserva.
 * Encargado de la lógica CRUD para los tipos de reserva (HOTEL, VUELO, CRUCERO, etc.).
 */
@Service
public class TipoReservaServiceImpl implements TipoReservaService {

    @Autowired
    private TipoReservaRepository tipoReservaRepository;

    /**
     * Obtiene todos los tipos de reserva registrados en el sistema.
     */
    @Override
    public List<TipoReservaEntity> findAll() {
        return tipoReservaRepository.findAll();
    }

    /**
     * Busca un tipo de reserva por su identificador.
     */
    @Override
    public TipoReservaEntity findById(Integer idTipoReserva) {
        return tipoReservaRepository.findById(idTipoReserva).orElse(null);
    }

    /**
     * Guarda o actualiza un tipo de reserva en la base de datos.
     */
    @Override
    public TipoReservaEntity save(TipoReservaEntity tipoReserva) {
        return tipoReservaRepository.save(tipoReserva);
    }

    /**
     * Elimina un tipo de reserva por su identificador.
     */
    @Override
    public void deleteById(Integer idTipoReserva) {
        tipoReservaRepository.deleteById(idTipoReserva);
    }
}