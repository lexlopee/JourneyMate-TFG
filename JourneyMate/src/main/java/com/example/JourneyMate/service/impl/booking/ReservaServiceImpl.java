package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.service.booking.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Override
    public List<ReservaEntity> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<ReservaEntity> findById(Integer idReserva) {
        return reservaRepository.findById(idReserva);
    }

    @Override
    public ReservaEntity crear(ReservaEntity reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    public ReservaEntity actualizar(Integer idReserva, ReservaEntity reserva) {
        Optional<ReservaEntity> existente = reservaRepository.findById(idReserva);

        if (existente.isEmpty()) {
            return null;
        }
        reserva.setIdReserva(idReserva);
        return reservaRepository.save(reserva);
    }

    @Override
    public void deleteById(Integer idReserva) {
        reservaRepository.deleteById(idReserva);
    }

    @Override
    public boolean existsById(Integer idReserva) {
        return reservaRepository.existsById(idReserva);
    }

    @Override
    public List<ReservaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return reservaRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    public List<ReservaEntity> findByEstadoNombre(String estado) {
        return reservaRepository.findByEstadoName(estado);
    }

    @Override
    public List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin) {
        return reservaRepository.findByFechaReservaBetween(inicio, fin);
    }
}
