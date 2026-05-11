package com.example.JourneyMate.controller.booking;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.dao.booking.ReservaRepository;
import com.example.JourneyMate.dto.reserva.ReservaListDTO;
import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.dto.reserva.ReservaResponseDTO;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.service.booking.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
/**
 * Controlador encargado de gestionar las operaciones relacionadas
 * con las reservas dentro del sistema JourneyMate.
 * <p>
 * Permite crear, consultar, actualizar y eliminar reservas,
 * además de gestionar su estado e historial.
 */
@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {
    /**
     * Servicio encargado de la lógica de negocio de reservas.
     */
    @Autowired
    private ReservaService reservaService;
    /**
     * Repositorio para operaciones directas sobre reservas.
     */
    @Autowired
    private ReservaRepository reservaRepository;
    /**
     * Repositorio para la gestión de estados de reserva.
     */
    @Autowired
    private EstadoRepository estadoRepository;
    /**
     * Obtiene todas las reservas registradas.
     *
     * @return lista completa de reservas
     */
    @GetMapping
    public ResponseEntity<List<ReservaEntity>> findAll() {
        return ResponseEntity.ok(reservaService.findAll());
    }
    /**
     * Busca una reserva por su identificador.
     *
     * @param id identificador de la reserva
     * @return la reserva encontrada o respuesta 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaEntity> findById(@PathVariable Integer id) {
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Obtiene las reservas asociadas a un usuario específico.
     *
     * @param idUsuario identificador del usuario
     * @return lista de reservas del usuario
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaListDTO>> findByUsuario(@PathVariable Integer idUsuario) {
        List<ReservaListDTO> result = reservaService.findDTOsByUsuarioId(idUsuario);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene el historial completo de reservas de un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return historial completo de reservas
     */
    @GetMapping("/usuario/{idUsuario}/historial")
    public ResponseEntity<List<ReservaListDTO>> findHistorial(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(reservaService.findHistorialByUsuarioId(idUsuario));
    }
    /**
     * Busca reservas según el nombre de su estado.
     *
     * @param nombreEstado nombre del estado de la reserva
     * @return lista de reservas filtradas por estado
     */
    @GetMapping("/estado/{nombreEstado}")
    public ResponseEntity<List<ReservaEntity>> findByEstado(@PathVariable String nombreEstado) {
        return ResponseEntity.ok(reservaService.findByEstadoNombre(nombreEstado));
    }
    /**
     * Obtiene las reservas registradas dentro de un rango de fechas.
     *
     * @param inicio fecha inicial del rango
     * @param fin fecha final del rango
     * @return lista de reservas encontradas
     */
    @GetMapping("/fecha")
    public ResponseEntity<List<ReservaEntity>> findByFecha(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {
        return ResponseEntity.ok(reservaService.findByFechaReservaBetween(inicio, fin));
    }
    /**
     * Crea una reserva completa dentro del sistema.
     * <p>
     * El estado inicial de la reserva se establece automáticamente
     * como PENDIENTE.
     *
     * @param dto datos necesarios para crear la reserva
     * @return información resumida de la reserva creada
     */
    @PostMapping("/completa")
    public ResponseEntity<?> createCompleta(@RequestBody ReservaRequestDTO dto) {
        try {
            // ⭐ Forzamos estado PENDIENTE (id=1) siempre al crear
            dto.setIdEstado(1);

            ReservaEntity reserva = reservaService.crearCompleta(dto);

            ReservaResponseDTO response = new ReservaResponseDTO();
            response.setIdReserva(reserva.getIdReserva());
            response.setIdUsuario(reserva.getUsuario().getIdUsuario());
            response.setIdServicio(reserva.getServicio().getIdServicio());
            response.setIdEstado(reserva.getEstado().getIdEstado());
            response.setIdTipoReserva(reserva.getTipoReserva().getIdTipoReserva());
            response.setPrecioTotal(reserva.getPrecioTotal());
            response.setFechaReserva(reserva.getFechaReserva());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR: " + e.getMessage());
        }
    }

    /**
     * Actualiza el estado de una reserva existente.
     * <p>
     * Este endpoint es utilizado principalmente por integraciones
     * de pago como Stripe o PayPal tras una transacción exitosa.
     *
     * @param id identificador de la reserva
     * @param body cuerpo de la petición con el nuevo estado
     * @return información del nuevo estado actualizado
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            String nombreEstado = body.get("estado");
            if (nombreEstado == null || nombreEstado.isBlank()) {
                return ResponseEntity.badRequest().body("Campo 'estado' requerido");
            }

            ReservaEntity reserva = reservaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + id));

            EstadoEntity estado = estadoRepository.findByNombreIgnoreCase(nombreEstado)
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + nombreEstado));

            reserva.setEstado(estado);
            reservaRepository.save(reserva);

            return ResponseEntity.ok(Map.of(
                    "idReserva", id,
                    "estadoNuevo", estado.getNombre()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getMessage());
        }
    }
    /**
     * Actualiza la información de una reserva existente.
     *
     * @param id identificador de la reserva
     * @param reserva datos actualizados de la reserva
     * @return reserva actualizada o respuesta 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservaEntity> update(@PathVariable Integer id, @RequestBody ReservaEntity reserva) {
        ReservaEntity updated = reservaService.actualizar(id, reserva);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    /**
     * Elimina una reserva por su identificador.
     *
     * @param id identificador de la reserva
     * @return respuesta vacía si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!reservaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}