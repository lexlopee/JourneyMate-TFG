package com.example.JourneyMate.controller.service;

import com.example.JourneyMate.dto.service.ServicioTuristicoRequestDTO;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.service.service.ServicioTuristicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServicioTuristicoController {

    private final ServicioTuristicoService servicioTuristicoService;

    @GetMapping
    public ResponseEntity<List<ServicioTuristicoEntity>> findAll() {
        return ResponseEntity.ok(servicioTuristicoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioTuristicoEntity> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(servicioTuristicoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServicioTuristicoEntity> create(@RequestBody ServicioTuristicoRequestDTO dto) {
        ServicioTuristicoEntity servicio = new ServicioTuristicoEntity();
        servicio.setNombre(dto.getNombre());
        // ✅ ELIMINADOS: setDescripcion, setTipo, setEstrellas
        // Esos campos ya no existen en ServicioTuristicoEntity (solo en las subclases).
        // Para crear un hotel/apartamento con esos campos usa el endpoint /api/v1/reservas/completa
        servicio.setPrecioBase(dto.getPrecioBase());

        return ResponseEntity.ok(servicioTuristicoService.save(servicio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioTuristicoEntity> update(
            @PathVariable Integer id,
            @RequestBody ServicioTuristicoRequestDTO dto) {

        ServicioTuristicoEntity servicio = servicioTuristicoService.findById(id);
        servicio.setNombre(dto.getNombre());
        // ✅ ELIMINADOS: setDescripcion, setTipo, setEstrellas (mismo motivo)
        servicio.setPrecioBase(dto.getPrecioBase());

        return ResponseEntity.ok(servicioTuristicoService.save(servicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicioTuristicoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}