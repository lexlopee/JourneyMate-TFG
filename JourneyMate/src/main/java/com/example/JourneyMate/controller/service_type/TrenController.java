package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.TrenEntity;
import com.example.JourneyMate.service.service_type.TrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los trenes del sistema.
 *
 * Permite listar, consultar, crear, actualizar y eliminar trenes
 * que heredan de {@link com.example.JourneyMate.entity.service.ServicioTuristicoEntity}.
 */
@RestController
@RequestMapping("/trenes")
public class TrenController {

    @Autowired
    private TrenService trenService;

    /**
     * Obtiene la lista de todos los trenes disponibles.
     *
     * @return lista de {@link TrenEntity}
     */
    @GetMapping
    public ResponseEntity<List<TrenEntity>> findAll() {
        return ResponseEntity.ok(trenService.findAll());
    }

    /**
     * Obtiene un tren por su identificador.
     *
     * @param id identificador del tren
     * @return entidad {@link TrenEntity} si existe,
     *         o respuesta 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrenEntity> findById(@PathVariable Integer id) {
        TrenEntity tren = trenService.findById(id);
        return tren != null
                ? ResponseEntity.ok(tren)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea un nuevo tren en el sistema.
     *
     * @param tren entidad del tren a crear
     * @return tren creado {@link TrenEntity}
     */
    @PostMapping
    public ResponseEntity<TrenEntity> create(@RequestBody TrenEntity tren) {
        return ResponseEntity.ok(trenService.save(tren));
    }

    /**
     * Actualiza un tren existente.
     *
     * Si el tren no existe, devuelve una respuesta 404.
     *
     * @param id identificador del tren a actualizar
     * @param tren datos actualizados del tren
     * @return tren actualizado {@link TrenEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<TrenEntity> update(@PathVariable Integer id,
                                             @RequestBody TrenEntity tren) {

        if (trenService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        // porque hereda de ServicioTuristicoEntity
        tren.setIdServicio(id);

        return ResponseEntity.ok(trenService.save(tren));
    }

    /**
     * Elimina un tren por su identificador.
     *
     * Si el tren no existe, devuelve 404.
     *
     * @param id identificador del tren a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (trenService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        trenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}