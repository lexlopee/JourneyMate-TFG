package com.example.JourneyMate.controller.booking;

import com.example.JourneyMate.dto.reserva.ReservaRequestDTO;
import com.example.JourneyMate.dto.reserva.ReservaResponseDTO;
import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.service.booking.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.JourneyMate.dto.reserva.ReservaListDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaEntity>> findAll() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaEntity> findById(@PathVariable Integer id) {
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaListDTO>> findByUsuario(@PathVariable Integer idUsuario) {
        // ⭐ Llama al nuevo método del service que usa la query JPQL directa
        List<ReservaListDTO> result = reservaService.findDTOsByUsuarioId(idUsuario);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/estado/{nombreEstado}")
    public ResponseEntity<List<ReservaEntity>> findByEstado(@PathVariable String nombreEstado) {
        return ResponseEntity.ok(reservaService.findByEstadoNombre(nombreEstado));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<ReservaEntity>> findByFecha(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {
        return ResponseEntity.ok(reservaService.findByFechaReservaBetween(inicio, fin));
    }

    @PostMapping("/completa")
    public ResponseEntity<?> createCompleta(@RequestBody ReservaRequestDTO dto) {
        try {
            ReservaEntity reserva = reservaService.crearCompleta(dto);

            // ✅ SOLUCIÓN: devolver solo un DTO simple con el ID y los datos básicos.
            // Devolver la ReservaEntity completa causa bucles infinitos en la
            // serialización JSON (ServicioTuristicoEntity → subclase → padre → ...)
            // lo que rompe la respuesta HTTP con ERR_INCOMPLETE_CHUNKED_ENCODING.
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

    @PutMapping("/{id}")
    public ResponseEntity<ReservaEntity> update(@PathVariable Integer id, @RequestBody ReservaEntity reserva) {
        ReservaEntity updated = reservaService.actualizar(id, reserva);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!reservaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}