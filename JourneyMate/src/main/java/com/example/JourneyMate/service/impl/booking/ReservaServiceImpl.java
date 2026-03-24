package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.booking.TipoReservaRepository;
import com.example.JourneyMate.dao.service_type.*;
import com.example.JourneyMate.dao.user.UsuarioRepository;
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

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired private EstadoRepository estadoRepository;
    @Autowired private TipoReservaRepository tipoReservaRepository;
    @Autowired private DireccionRepository direccionRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private ApartamentoRepository apartamentoRepository;
    @Autowired private ActividadRepository actividadRepository;
    @Autowired private CruceroRepository cruceroRepository;
    @Autowired private VTCRepository vtcRepository;
    @Autowired private VueloRepository vueloRepository;
    @Autowired private TrenRepository trenRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // ELIMINADO: ServicioTuristicoRepository ya no se usa aquí.
    // La inserción en servicio_turistico la hace JPA automáticamente
    // al guardar la entidad específica (HotelEntity, ApartamentoEntity, etc.)
    // gracias a @Inheritance(JOINED) + @PrimaryKeyJoinColumn.

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

        // 1. Cargar entidades de referencia
        UsuarioEntity usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dto.getIdUsuario()));

        TipoReservaEntity tipo = tipoReservaRepository.findById(dto.getIdTipoReserva())
                .orElseThrow(() -> new RuntimeException("Tipo de reserva no encontrado: " + dto.getIdTipoReserva()));

        EstadoEntity estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + dto.getIdEstado()));

        ServicioTuristicoRequestDTO s = dto.getServicio();

        // 2. Crear la entidad específica según tipo.
        //    JPA con @Inheritance(JOINED) insertará automáticamente:
        //      - una fila en servicio_turistico (nombre, precio_base)
        //      - una fila en la tabla hija (hotel, apartamento, etc.)
        //    con el mismo id_servicio. NO hay que guardar ServicioTuristicoEntity por separado.

        // servicioGuardado es la entidad base que usaremos en la Reserva
        var servicioGuardado = switch (s.getTipo()) {

            case "HOTEL" -> {
                HotelEntity h = new HotelEntity();
                h.setNombre(s.getNombre());
                h.setPrecioBase(s.getPrecioBase());
                // ✅ CORRECCIÓN: la constraint ck_estr_hote exige estrellas BETWEEN 1 AND 5
                // Si viene null, 0 o fuera de rango → usamos 1 como mínimo válido
                Integer estrellas = s.getEstrellas();
                if (estrellas == null || estrellas < 1 || estrellas > 5) {
                    estrellas = 1;
                }
                h.setEstrellas(estrellas);
                h.setDescripcion(s.getDescripcion());
                yield hotelRepository.save(h);
            }

            case "APARTAMENTO" -> {
                ApartamentoEntity a = new ApartamentoEntity();
                a.setNombre(s.getNombre());
                a.setPrecioBase(s.getPrecioBase());
                a.setDescripcion(s.getDescripcion());
                yield apartamentoRepository.save(a);
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
                c.setFechaLlegada(s.getFechaLlegada());
                yield cruceroRepository.save(c);
            }

            case "VTC" -> {
                VTCEntity v = new VTCEntity();
                v.setNombre(s.getNombre());
                v.setPrecioBase(s.getPrecioBase());
                v.setMarca(s.getMarca());
                v.setModelo(s.getModelo());
                v.setDistancia(s.getDistancia() != null ? String.valueOf(new BigDecimal(s.getDistancia())) : null);
                v.setPrecio(s.getPrecioBase());
                yield vtcRepository.save(v);
            }

            case "VUELO" -> {
                VueloEntity v = new VueloEntity();
                v.setNombre(s.getNombre());
                v.setPrecioBase(s.getPrecioBase());
                v.setCompañia(s.getCompañia());
                v.setOrigen(s.getOrigen());
                v.setDestino(s.getDestino());
                v.setFechaSalida(s.getFechaSalida());
                v.setFechaLlegada(s.getFechaLlegada());
                yield vueloRepository.save(v);
            }

            case "TREN" -> {
                TrenEntity t = new TrenEntity();
                t.setNombre(s.getNombre());
                t.setPrecioBase(s.getPrecioBase());
                t.setCompañia(s.getCompañia());
                t.setOrigen(s.getOrigen());
                t.setDestino(s.getDestino());
                t.setFechaSalida(s.getFechaSalida());
                t.setFechaLlegada(s.getFechaLlegada());
                yield trenRepository.save(t);
            }

            default -> throw new RuntimeException("Tipo de servicio no reconocido: " + s.getTipo());
        };

        // 3. Dirección opcional (usa el id_servicio ya generado)
        if (s.getLatitud() != null && s.getLongitud() != null) {
            DireccionEntity dir = new DireccionEntity();
            dir.setServicio(servicioGuardado);
            dir.setCalle(s.getCalle());
            dir.setNumero(s.getNumero());
            dir.setCiudad(s.getCiudad());
            dir.setLatitud(s.getLatitud());
            dir.setLongitud(s.getLongitud());
            direccionRepository.save(dir);
        }

        // 4. Crear la reserva enlazando todo
        ReservaEntity reserva = new ReservaEntity();
        reserva.setUsuario(usuario);
        reserva.setServicio(servicioGuardado);   // apunta al servicio_turistico recién creado
        reserva.setTipoReserva(tipo);
        reserva.setEstado(estado);
        reserva.setPrecioTotal(dto.getPrecioTotal());
        reserva.setFechaReserva(LocalDate.now());

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
}