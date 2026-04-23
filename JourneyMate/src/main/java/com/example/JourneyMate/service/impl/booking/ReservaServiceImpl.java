package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.booking.TipoReservaRepository;
import com.example.JourneyMate.dao.service_type.*;
import com.example.JourneyMate.dao.user.UsuarioRepository;
import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.dto.service.ServicioTuristicoRequestDTO;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.entity.booking.TipoReservaEntity;
import com.example.JourneyMate.entity.service_type.*;
import com.example.JourneyMate.entity.user.UsuarioEntity;
import com.example.JourneyMate.service.booking.ReservaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private EstadoRepository estadoRepository;
    @Autowired
    private TipoReservaRepository tipoReservaRepository;
    @Autowired
    private DireccionRepository direccionRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private CruceroRepository cruceroRepository;
    @Autowired
    private CocheRepository cocheRepository;
    @Autowired
    private VueloRepository vueloRepository;
    @Autowired
    private TrenRepository trenRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // ── Convierte Object[] de SQL nativo a ReservaListDTO ────────────────────
    private ReservaListDTO mapRow(Object[] row) {
        ReservaListDTO dto = new ReservaListDTO();
        dto.setIdReserva(row[0] != null ? ((Number) row[0]).intValue() : null);
        dto.setServicioNombre(row[1] != null ? row[1].toString() : null);
        dto.setPrecioTotal(row[2] != null ? new BigDecimal(row[2].toString()) : null);
        dto.setEstadoNombre(row[3] != null ? row[3].toString() : null);
        dto.setTipoReservaNombre(row[4] != null ? row[4].toString() : null);
        if (row[5] != null) {
            dto.setFechaReserva(((java.sql.Date) row[5]).toLocalDate());
        }
        dto.setIdServicio(row[6] != null ? ((Number) row[6]).intValue() : null);
        dto.setIdTipoReserva(row[7] != null ? ((Number) row[7]).intValue() : null);
        dto.setPrecioBase(row[8] != null ? new BigDecimal(row[8].toString()) : null);
        return dto;
    }

    @Override
    public List<ReservaEntity> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<ReservaEntity> findById(Integer idReserva) {
        return reservaRepository.findById(idReserva);
    }

    @Override
    @Transactional
    public ReservaEntity crearCompleta(ReservaRequestDTO dto) {

        UsuarioEntity usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario()));

        TipoReservaEntity tipo = tipoReservaRepository.findById(dto.getIdTipoReserva())
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado: " + dto.getIdTipoReserva()));

        EstadoEntity estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + dto.getIdEstado()));

        ServicioTuristicoRequestDTO s = dto.getServicio();

        var servicioGuardado = switch (s.getTipo()) {

            case "HOTEL" -> {
                HotelEntity h = new HotelEntity();
                h.setNombre(s.getNombre());
                h.setPrecioBase(s.getPrecioBase());
                Integer estrellas = s.getEstrellas();
                if (estrellas == null || estrellas < 1 || estrellas > 5) estrellas = 1;
                h.setEstrellas(estrellas);
                h.setDescripcion(s.getDescripcion());
                yield hotelRepository.save(h);
            }

            case "ACTIVIDAD" -> {
                ActividadEntity a = new ActividadEntity();
                a.setNombre(s.getNombre());
                a.setPrecioBase(s.getPrecioBase());
                a.setDescripcion(s.getDescripcion());
                yield actividadRepository.save(a);
            }

            case "CRUCERO" -> {
                CruceroEntity c = new CruceroEntity();
                c.setNombre(s.getNombre());
                c.setPrecioBase(s.getPrecioBase());
                c.setPuertoSalida(s.getOrigen());
                c.setPuertoLlegada(s.getDestino());
                c.setNaviera(s.getNaviera());
                c.setFechaSalida(s.getFechaSalida());
                c.setFechaLlegada(s.getFechaRegreso());
                yield cruceroRepository.save(c);
            }

            case "VTC", "COCHE", "Coche", "coche" -> {
                CocheEntity v = new CocheEntity();
                v.setNombre(s.getNombre());
                v.setPrecioBase(s.getPrecioBase());
                v.setMarca(s.getMarca());
                v.setModelo(s.getModelo());
                v.setDistancia(s.getDistancia());
                v.setPrecio(s.getPrecioBase());
                yield cocheRepository.save(v);
            }

            case "VUELO" -> {
                VueloEntity v = new VueloEntity();
                v.setNombre(s.getNombre());
                v.setPrecioBase(s.getPrecioBase());
                v.setCompania(s.getCompania());
                v.setOrigen(s.getOrigen());
                v.setDestino(s.getDestino());
                v.setFechaSalida(s.getFechaSalida());
                v.setFechaRegreso(s.getFechaRegreso());
                yield vueloRepository.save(v);
            }

            case "TREN" -> {
                TrenEntity t = new TrenEntity();
                t.setNombre(s.getNombre());
                t.setPrecioBase(s.getPrecioBase());
                t.setCompania(s.getCompania());
                t.setOrigen(s.getOrigen());
                t.setDestino(s.getDestino());
                t.setFechaSalida(s.getFechaSalida());
                t.setFechaLlegada(s.getFechaRegreso());
                yield trenRepository.save(t);
            }

            default -> throw new RuntimeException("Tipo no reconocido: " + s.getTipo());
        };

        if (s.getLatitud() != null && s.getLongitud() != null) {
            DireccionEntity dir = new DireccionEntity();
            dir.setServicio(servicioGuardado);
            dir.setDescripcion(s.getDescripcion_direccion() != null
                    ? s.getDescripcion_direccion() : s.getNombre());
            dir.setLatitud(s.getLatitud());
            dir.setLongitud(s.getLongitud());
            direccionRepository.save(dir);
        }

        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(usuario);
        reserva.setServicio(servicioGuardado);
        reserva.setTipoReserva(tipo);
        reserva.setEstado(estado);
        reserva.setPrecioTotal(dto.getPrecioTotal());

        // ✅ CLAVE: usar la fecha del servicio si viene, si no usar hoy como fallback
        // fechaReserva = fecha de INICIO del servicio (check-in, salida, etc.)
        // Esta es la fecha que se usa para la lógica de cancelación en el frontend
        LocalDate fechaAGuardar = dto.getFechaServicio() != null
                ? dto.getFechaServicio()
                : LocalDate.now();
        reserva.setFechaReserva(fechaAGuardar);

        return reservaRepository.save(reserva);
    }

    @Override
    public ReservaEntity actualizar(Integer idReserva, ReservaEntity reserva) {
        if (reservaRepository.findById(idReserva).isEmpty()) return null;
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

    @Override
    public List<ReservaListDTO> findDTOsByUsuarioId(Integer idUsuario) {
        return reservaRepository.findRawDTOsByUsuarioId(idUsuario)
                .stream().map(this::mapRow).collect(Collectors.toList());
    }

    @Override
    public List<ReservaListDTO> findHistorialByUsuarioId(Integer idUsuario) {
        return reservaRepository.findRawHistorialByUsuarioId(idUsuario)
                .stream().map(this::mapRow).collect(Collectors.toList());
    }
}