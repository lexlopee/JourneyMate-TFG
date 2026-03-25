package com.example.JourneyMate.controller.user;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import com.example.JourneyMate.service.user.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        try {
            UsuarioEntity usuario = usuarioService.findById(id);
            if (usuario != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("idUsuario", usuario.getIdUsuario());
                response.put("nombre", usuario.getNombre());
                response.put("email", usuario.getEmail());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Esto te dirá en la consola de Java qué está pasando realmente
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioEntity> create(@RequestBody UsuarioEntity usuario) {
        return ResponseEntity.ok(usuarioService.save(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> update(@PathVariable Integer id, @RequestBody UsuarioEntity usuario) {
        if (usuarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        usuario.setIdUsuario(id);
        return ResponseEntity.ok(usuarioService.save(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (usuarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
