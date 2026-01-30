package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.TrenEntity;
import com.example.JourneyMate.service.service_type.TrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trenes")
public class TrenController {

    @Autowired
    private TrenService trenService;

    @GetMapping
    public ResponseEntity<List<TrenEntity>> findAll() {
        return ResponseEntity.ok(trenService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrenEntity> findById(@PathVariable Integer id) {
        TrenEntity tren = trenService.findById(id);
        return tren != null ? ResponseEntity.ok(tren) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TrenEntity> create(@RequestBody TrenEntity tren) {
        return ResponseEntity.ok(trenService.save(tren));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrenEntity> update(@PathVariable Integer id, @RequestBody TrenEntity tren) {
        if (trenService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        tren.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(trenService.save(tren));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (trenService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        trenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
