package com.example.JourneyMate.controller.route;

import com.example.JourneyMate.entity.route.RutaEntity;
import com.example.JourneyMate.service.route.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar rutas dentro del sistema JourneyMate.
 * <p>
 * Permite realizar operaciones CRUD sobre rutas y consultar
 * rutas asociadas a usuarios específicos.
 */
@RestController
@RequestMapping("/rutas")
public class RutaController {

    /**
     * Servicio encargado de la lógica de negocio relacionada con rutas.
     */
    @Autowired
    private RutaService rutaService;

    /**
     * Obtiene todas las rutas registradas.
     *
     * @return lista completa de rutas
     */
    @GetMapping
    public ResponseEntity<List<RutaEntity>> findAll() {

        return ResponseEntity.ok(
                rutaService.findAll()
        );
    }

    /**
     * Busca una ruta por su identificador.
     *
     * @param id identificador de la ruta
     * @return ruta encontrada o respuesta 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<RutaEntity> findById(
            @PathVariable Integer id) {

        RutaEntity ruta = rutaService.findById(id);

        return ruta != null
                ? ResponseEntity.ok(ruta)
                : ResponseEntity.notFound().build();
    }

    /**
     * Obtiene todas las rutas asociadas a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de rutas del usuario
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<RutaEntity>> findByUsuario(
            @PathVariable Integer idUsuario) {

        return ResponseEntity.ok(
                rutaService.findByUsuarioIdUsuario(idUsuario)
        );
    }

    /**
     * Crea una nueva ruta en el sistema.
     *
     * @param ruta información de la ruta
     * @return ruta creada
     */
    @PostMapping
    public ResponseEntity<RutaEntity> create(
            @RequestBody RutaEntity ruta) {

        return ResponseEntity.ok(
                rutaService.saveRuta(ruta)
        );
    }

    /**
     * Actualiza la información de una ruta existente.
     *
     * @param id identificador de la ruta
     * @param ruta nueva información de la ruta
     * @return ruta actualizada o respuesta 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<RutaEntity> update(
            @PathVariable Integer id,
            @RequestBody RutaEntity ruta) {

        if (rutaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        ruta.setIdRuta(id);

        return ResponseEntity.ok(
                rutaService.saveRuta(ruta)
        );
    }

    /**
     * Elimina una ruta por su identificador.
     *
     * @param id identificador de la ruta
     * @return respuesta vacía si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id) {

        if (rutaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        rutaService.deleteBy(id);

        return ResponseEntity.noContent().build();
    }
}