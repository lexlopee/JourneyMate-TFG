package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.VueloEntity;
import com.example.JourneyMate.service.service_type.VueloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los vuelos del sistema.
 *
 * Permite listar, consultar, crear, actualizar y eliminar vuelos
 * que heredan de {@link com.example.JourneyMate.entity.service.ServicioTuristicoEntity}.
 */
@RestController
@RequestMapping("/vuelos")
public class VueloController {

    @Autowired
    private VueloService vueloService;

    /**
     * Obtiene la lista de todos los vuelos disponibles.
     *
     * @return lista de {@link VueloEntity}
     */
    @GetMapping
    public ResponseEntity<List<VueloEntity>> findAll() {
        return ResponseEntity.ok(vueloService.findAll());
    }

    /**
     * Obtiene un vuelo por su identificador.
     *
     * @param id identificador del vuelo
     * @return entidad {@link VueloEntity} si existe,
     *         o respuesta 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<VueloEntity> findById(@PathVariable Integer id) {
        VueloEntity vuelo = vueloService.findById(id);
        return vuelo != null
                ? ResponseEntity.ok(vuelo)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea un nuevo vuelo en el sistema.
     *
     * @param vuelo entidad del vuelo a crear
     * @return vuelo creado {@link VueloEntity}
     */
    @PostMapping
    public ResponseEntity<VueloEntity> create(@RequestBody VueloEntity vuelo) {
        return ResponseEntity.ok(vueloService.save(vuelo));
    }

    /**
     * Actualiza un vuelo existente.
     *
     * Si el vuelo no existe, devuelve una respuesta 404.
     *
     * @param id identificador del vuelo a actualizar
     * @param vuelo datos actualizados del vuelo
     * @return vuelo actualizado {@link VueloEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<VueloEntity> update(@PathVariable Integer id,
                                              @RequestBody VueloEntity vuelo) {

        if (vueloService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        vuelo.setIdServicio(id);

        return ResponseEntity.ok(vueloService.save(vuelo));
    }

    /**
     * Elimina un vuelo por su identificador.
     *
     * Si el vuelo no existe, devuelve 404.
     *
     * @param id identificador del vuelo a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (vueloService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        vueloService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}