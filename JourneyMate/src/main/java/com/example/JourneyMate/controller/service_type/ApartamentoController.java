package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.ApartamentoEntity;
import com.example.JourneyMate.service.service_type.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartamentos")
public class ApartamentoController {

    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping
    public ResponseEntity<List<ApartamentoEntity>> findAll() {
        return ResponseEntity.ok(apartamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartamentoEntity> findById(@PathVariable Integer id) {
        ApartamentoEntity apartamento = apartamentoService.findById(id);
        return apartamento != null ? ResponseEntity.ok(apartamento) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ApartamentoEntity> create(@RequestBody ApartamentoEntity apartamento) {
        return ResponseEntity.ok(apartamentoService.save(apartamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartamentoEntity> update(@PathVariable Integer id, @RequestBody ApartamentoEntity apartamento) {
        if (apartamentoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        apartamento.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(apartamentoService.save(apartamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (apartamentoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        apartamentoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
