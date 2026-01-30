package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.ActividadEntity;
import com.example.JourneyMate.service.service_type.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividades")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    @GetMapping
    public ResponseEntity<List<ActividadEntity>> findAll() {
        return ResponseEntity.ok(actividadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadEntity> findById(@PathVariable Integer id) {
        ActividadEntity actividad = actividadService.findByIdActividad(id);
        return actividad != null ? ResponseEntity.ok(actividad) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ActividadEntity> create(@RequestBody ActividadEntity actividad) {
        return ResponseEntity.ok(actividadService.saveActividad(actividad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadEntity> update(@PathVariable Integer id, @RequestBody ActividadEntity actividad) {
        if (actividadService.findByIdActividad(id) == null) {
            return ResponseEntity.notFound().build();
        }
        actividad.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(actividadService.saveActividad(actividad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (actividadService.findByIdActividad(id) == null) {
            return ResponseEntity.notFound().build();
        }
        actividadService.deleteByIdActividad(id);
        return ResponseEntity.noContent().build();
    }
}
