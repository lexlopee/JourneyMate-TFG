package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.VueloEntity;
import com.example.JourneyMate.service.service_type.VueloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vuelos")
public class VueloController {

    @Autowired
    private VueloService vueloService;

    @GetMapping
    public ResponseEntity<List<VueloEntity>> findAll() {
        return ResponseEntity.ok(vueloService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VueloEntity> findById(@PathVariable Integer id) {
        VueloEntity vuelo = vueloService.findById(id);
        return vuelo != null ? ResponseEntity.ok(vuelo) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VueloEntity> create(@RequestBody VueloEntity vuelo) {
        return ResponseEntity.ok(vueloService.save(vuelo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VueloEntity> update(@PathVariable Integer id, @RequestBody VueloEntity vuelo) {
        if (vueloService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        vuelo.setIdServicio(id);
        return ResponseEntity.ok(vueloService.save(vuelo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (vueloService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        vueloService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
