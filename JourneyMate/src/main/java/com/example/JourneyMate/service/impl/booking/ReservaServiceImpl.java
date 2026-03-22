package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.service.ServicioTuristicoRepository;
import com.example.JourneyMate.dao.user.UsuarioRepository;
import com.example.JourneyMate.dto.mapper.booking.ReservaMapper;
import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;
import com.example.JourneyMate.service.booking.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.dao.service.ServicioTuristicoRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ServicioTuristicoRepository servicioTuristicoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // ⭐ AÑADIR ESTO

    @Override
    public List<ReservaEntity> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<ReservaEntity> findById(Integer idReserva) {
        return reservaRepository.findById(idReserva);
    }

    // ⭐ MÉTODO CORRECTO PARA CREAR RESERVAS DESDE EL FRONTEND
    // ⭐ MÉTODO CORRECTO PARA CREAR RESERVAS DESDE EL FRONTEND
    @Override
    public ReservaEntity crear(ReservaRequestDTO reservaDTO) {

        // 1️⃣ Buscar usuario por idUsuario del DTO
        UsuarioEntity usuario = usuarioRepository.findById(reservaDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2️⃣ Crear servicio_turistico con los datos del hotel
        ServicioTuristicoEntity servicio = new ServicioTuristicoEntity();
        servicio.setNombre(reservaDTO.getNombreServicio());
        servicio.setPrecioBase(reservaDTO.getPrecioTotal());
        servicio = servicioTuristicoRepository.save(servicio);

        // 3️⃣ Crear la reserva
        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(usuario);
        reserva.setServicio(servicio);

        TipoReservaEntity tipo = new TipoReservaEntity();
        tipo.setIdTipoReserva(reservaDTO.getIdTipoReserva());
        reserva.setTipoReserva(tipo);

        reserva.setPrecioTotal(reservaDTO.getPrecioTotal());
        reserva.setFechaReserva(LocalDate.now());

        // Estado por defecto
        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(1); // PENDIENTE
        reserva.setEstado(estado);

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
        return reservaRepository.findByEstadoNombre(estado);
    }

    @Override
    public List<ReservaEntity> findByFechaReservaBetween(LocalDate inicio, LocalDate fin) {
        return reservaRepository.findByFechaReservaBetween(inicio, fin);
    }
}
