package com.example.JourneyMate.controller.route;

import com.example.JourneyMate.entity.route.RutaEntity;
import com.example.JourneyMate.service.route.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rutas")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<RutaEntity>> findAll() {
        return ResponseEntity.ok(rutaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaEntity> findById(@PathVariable Integer id) {
        RutaEntity ruta = rutaService.findById(id);
        return ruta != null ? ResponseEntity.ok(ruta) : ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<RutaEntity>> findByUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(rutaService.findByUsuarioIdUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<RutaEntity> create(@RequestBody RutaEntity ruta) {
        return ResponseEntity.ok(rutaService.saveRuta(ruta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutaEntity> update(@PathVariable Integer id, @RequestBody RutaEntity ruta) {
        if (rutaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        ruta.setIdRuta(id);
        return ResponseEntity.ok(rutaService.saveRuta(ruta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (rutaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        rutaService.deleteBy(id);
        return ResponseEntity.noContent().build();
    }
}
