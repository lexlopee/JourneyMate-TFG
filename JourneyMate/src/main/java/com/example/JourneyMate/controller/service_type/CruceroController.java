package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.CruceroEntity;
import com.example.JourneyMate.service.service_type.CruceroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cruceros")
public class CruceroController {

    @Autowired
    private CruceroService cruceroService;

    @GetMapping
    public ResponseEntity<List<CruceroEntity>> findAll() {
        return ResponseEntity.ok(cruceroService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CruceroEntity> findById(@PathVariable Integer id) {
        CruceroEntity crucero = cruceroService.findByIdCrucero(id);
        return crucero != null ? ResponseEntity.ok(crucero) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CruceroEntity> create(@RequestBody CruceroEntity crucero) {
        return ResponseEntity.ok(cruceroService.save(crucero));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CruceroEntity> update(@PathVariable Integer id, @RequestBody CruceroEntity crucero) {
        if (cruceroService.findByIdCrucero(id) == null) {
            return ResponseEntity.notFound().build();
        }
        crucero.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(cruceroService.save(crucero));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (cruceroService.findByIdCrucero(id) == null) {
            return ResponseEntity.notFound().build();
        }
        cruceroService.deleteByIdCrucero(id);
        return ResponseEntity.noContent().build();
    }
}
