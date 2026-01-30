package com.example.JourneyMate.controller.booking;

import com.example.JourneyMate.entity.booking.ReservaEntity;
import com.example.JourneyMate.service.booking.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
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
    public ResponseEntity<List<ReservaEntity>> findByUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(reservaService.findByUsuarioIdUsuario(idUsuario));
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

    @PostMapping
    public ResponseEntity<ReservaEntity> create(@RequestBody ReservaEntity reserva) {
        return ResponseEntity.ok(reservaService.crear(reserva));
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
