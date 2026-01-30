package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.VTCEntity;
import com.example.JourneyMate.service.service_type.VTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vtc")
public class VTCController {

    @Autowired
    private VTCService vtcService;

    @GetMapping
    public ResponseEntity<List<VTCEntity>> findAll() {
        return ResponseEntity.ok(vtcService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VTCEntity> findById(@PathVariable Integer id) {
        VTCEntity vtc = vtcService.findById(id);
        return vtc != null ? ResponseEntity.ok(vtc) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VTCEntity> create(@RequestBody VTCEntity vtc) {
        return ResponseEntity.ok(vtcService.save(vtc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VTCEntity> update(@PathVariable Integer id, @RequestBody VTCEntity vtc) {
        if (vtcService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        vtc.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(vtcService.save(vtc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (vtcService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        vtcService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
