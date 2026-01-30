package com.example.JourneyMate.controller.service;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.service.service.ServicioTuristicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
public class ServicioTuristicoController {

    @Autowired
    private ServicioTuristicoService servicioTuristicoService;

    @GetMapping
    public ResponseEntity<List<ServicioTuristicoEntity>> findAll() {
        return ResponseEntity.ok(servicioTuristicoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioTuristicoEntity> findById(@PathVariable Integer id) {
        ServicioTuristicoEntity servicio = servicioTuristicoService.findById(id);
        return servicio != null ? ResponseEntity.ok(servicio) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ServicioTuristicoEntity> create(@RequestBody ServicioTuristicoEntity servicio) {
        return ResponseEntity.ok(servicioTuristicoService.save(servicio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioTuristicoEntity> update(@PathVariable Integer id, @RequestBody ServicioTuristicoEntity servicio) {
        if (servicioTuristicoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        servicio.setIdServicio(id);
        return ResponseEntity.ok(servicioTuristicoService.save(servicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (servicioTuristicoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        servicioTuristicoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
