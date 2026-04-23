package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.CocheEntity;
import com.example.JourneyMate.service.service_type.CocheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vtc")
public class CocheController {

    @Autowired
    private CocheService cocheService;

    @GetMapping
    public ResponseEntity<List<CocheEntity>> findAll() {
        return ResponseEntity.ok(cocheService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CocheEntity> findById(@PathVariable Integer id) {
        CocheEntity vtc = cocheService.findById(id);
        return vtc != null ? ResponseEntity.ok(vtc) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CocheEntity> create(@RequestBody CocheEntity vtc) {
        return ResponseEntity.ok(cocheService.save(vtc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CocheEntity> update(@PathVariable Integer id, @RequestBody CocheEntity vtc) {
        if (cocheService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        vtc.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(cocheService.save(vtc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (cocheService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        cocheService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
