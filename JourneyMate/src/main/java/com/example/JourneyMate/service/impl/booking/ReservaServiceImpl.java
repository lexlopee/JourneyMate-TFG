package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dao.booking.TipoReservaRepository;
import com.example.JourneyMate.dao.service.ServicioTuristicoRepository;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private VTCRepository vtcRepository;
    @Autowired
    private VueloRepository vueloRepository;
    @Autowired
    private TrenRepository trenRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ServicioTuristicoRepository servicioTuristicoRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
                .orElseThrow(() -> new RuntimeException("Tipo de reserva no encontrado: " + dto.getIdTipoReserva()));

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

            case "VTC" -> {
                VTCEntity v = new VTCEntity();
                v.setNombre(s.getNombre());
                v.setPrecioBase(s.getPrecioBase());
                v.setMarca(s.getMarca());
                v.setModelo(s.getModelo());
                v.setDistancia(s.getDistancia());
                v.setPrecio(s.getPrecioBase());
                v.setHoraSalida(s.getHoraSalida());
                v.setHoraLlegada(s.getHoraLlegada());
                yield vtcRepository.save(v);
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

            default -> throw new RuntimeException("Tipo de servicio no reconocido: " + s.getTipo());
        };

        if (s.getLatitud() != null && s.getLongitud() != null) {
            DireccionEntity dir = new DireccionEntity();
            dir.setServicio(servicioGuardado);
            dir.setDescripcion(s.getDescripcion_direccion() != null ? s.getDescripcion_direccion() : s.getNombre());
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
    @Transactional // ⭐ ¡OBLIGATORIO para ejecutar executeUpdate() y mantener la atomicidad!
    public void deleteById(Integer idReserva) {

        // 1. Buscamos la reserva antes de borrarla para extraer la ID de su servicio
        Optional<ReservaEntity> reservaOpt = reservaRepository.findById(idReserva);
        if (reservaOpt.isEmpty()) {
            return; // Si no existe, no hacemos nada
        }

        Integer idServicio = reservaOpt.get().getServicio().getIdServicio();

        // 2. PRIMERO: Borramos la Reserva (la tabla "hija" que depende del servicio)
        // Si no hacemos esto primero, la base de datos bloqueará el borrado por Foreign Key.
        reservaRepository.deleteById(idReserva);

        // 3. SEGUNDO: Borramos los servicios específicos (las tablas hijas de servicio_turistico)
        // Usamos 'idServicio', no idReserva
        entityManager.createNativeQuery("DELETE FROM journeymate.vtc WHERE id_servicio = :id")
                .setParameter("id", idServicio).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM journeymate.tren WHERE id_servicio = :id")
                .setParameter("id", idServicio).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM journeymate.actividad WHERE id_servicio = :id")
                .setParameter("id", idServicio).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM journeymate.hotel WHERE id_servicio = :id")
                .setParameter("id", idServicio).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM journeymate.crucero WHERE id_servicio = :id")
                .setParameter("id", idServicio).executeUpdate();

        // NOTA: Añade aquí los demás servicios que tengas (apartamento, vuelo, etc)

        // 4. FINALMENTE: Borramos la entidad padre
        entityManager.createNativeQuery("DELETE FROM journeymate.servicio_turistico WHERE id_servicio = :id")
                .setParameter("id", idServicio).executeUpdate();
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
        return reservaRepository.findDTOsByUsuarioId(idUsuario);
    }
}