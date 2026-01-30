package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.HabitacionEntity;
import com.example.JourneyMate.service.service_type.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<HabitacionEntity>> findAll() {
        return ResponseEntity.ok(habitacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionEntity> findById(@PathVariable Integer id) {
        HabitacionEntity habitacion = habitacionService.findById(id);
        return habitacion != null ? ResponseEntity.ok(habitacion) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<HabitacionEntity> create(@RequestBody HabitacionEntity habitacion) {
        return ResponseEntity.ok(habitacionService.save(habitacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionEntity> update(@PathVariable Integer id, @RequestBody HabitacionEntity habitacion) {
        if (habitacionService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        habitacion.setIdHabitacion(id);
        return ResponseEntity.ok(habitacionService.save(habitacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (habitacionService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        habitacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 🔍 Filtro: habitaciones por hotel
    @GetMapping("/hotel/{idHotel}")
    public ResponseEntity<List<HabitacionEntity>> findByHotel(@PathVariable Integer idHotel) {
        return ResponseEntity.ok(habitacionService.findByHotelIdServicio(idHotel));
    }

    // 🔍 Filtro: habitaciones por capacidad mínima
    @GetMapping("/capacidad/{capacidad}")
    public ResponseEntity<List<HabitacionEntity>> findByCapacidad(@PathVariable Integer capacidad) {
        return ResponseEntity.ok(habitacionService.findByCapacidadGreaterThanEqual(capacidad));
    }
}
