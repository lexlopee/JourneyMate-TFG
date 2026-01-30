package com.example.JourneyMate.controller.interest;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import com.example.JourneyMate.service.interest.PuntoInteresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/puntos")
public class PuntoInteresController {

    @Autowired
    private PuntoInteresService puntoInteresService;

    @GetMapping
    public ResponseEntity<List<PuntoInteresEntity>> findAll() {
        return ResponseEntity.ok(puntoInteresService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoInteresEntity> findById(@PathVariable Integer id) {
        PuntoInteresEntity punto = puntoInteresService.findById(id);
        return punto != null ? ResponseEntity.ok(punto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PuntoInteresEntity> create(@RequestBody PuntoInteresEntity punto) {
        return ResponseEntity.ok(puntoInteresService.save(punto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuntoInteresEntity> update(@PathVariable Integer id, @RequestBody PuntoInteresEntity punto) {
        if (puntoInteresService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        punto.setIdPunto(id);
        return ResponseEntity.ok(puntoInteresService.save(punto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (puntoInteresService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        puntoInteresService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
