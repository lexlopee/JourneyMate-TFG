package com.example.JourneyMate.controller.user;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import com.example.JourneyMate.service.user.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los usuarios del sistema.
 *
 * Permite listar, consultar, crear, actualizar y eliminar usuarios.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return lista de {@link UsuarioEntity}
     */
    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * Este método devuelve un mapa con los datos básicos del usuario
     * en lugar de la entidad completa.
     *
     * @param id identificador del usuario
     * @return mapa con datos del usuario o 404 si no existe
     */
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
            // Registro del error en consola para depuración
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param usuario entidad del usuario a crear
     * @return usuario creado {@link UsuarioEntity}
     */
    @PostMapping
    public ResponseEntity<UsuarioEntity> create(@RequestBody UsuarioEntity usuario) {
        return ResponseEntity.ok(usuarioService.save(usuario));
    }

    /**
     * Actualiza un usuario existente.
     *
     * Si el usuario no existe, devuelve una respuesta 404.
     *
     * @param id identificador del usuario a actualizar
     * @param usuario datos actualizados del usuario
     * @return usuario actualizado {@link UsuarioEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> update(@PathVariable Integer id,
                                                @RequestBody UsuarioEntity usuario) {

        if (usuarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        usuario.setIdUsuario(id);
        return ResponseEntity.ok(usuarioService.save(usuario));
    }

    /**
     * Elimina un usuario por su identificador.
     *
     * Si el usuario no existe, devuelve 404.
     *
     * @param id identificador del usuario a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (usuarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}