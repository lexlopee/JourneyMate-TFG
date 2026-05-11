package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.CocheEntity;
import com.example.JourneyMate.service.service_type.CocheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los coches (VTC) del sistema.
 *
 * Permite listar, consultar, crear, actualizar y eliminar coches
 * que heredan de {@link com.example.JourneyMate.entity.service.ServicioTuristicoEntity}.
 */
@RestController
@RequestMapping("/vtc")
public class CocheController {

    @Autowired
    private CocheService cocheService;

    /**
     * Obtiene la lista de todos los coches disponibles.
     *
     * @return lista de {@link CocheEntity}
     */
    @GetMapping
    public ResponseEntity<List<CocheEntity>> findAll() {
        return ResponseEntity.ok(cocheService.findAll());
    }

    /**
     * Obtiene un coche por su identificador.
     *
     * @param id identificador del coche
     * @return entidad {@link CocheEntity} si existe,
     *         o respuesta 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<CocheEntity> findById(@PathVariable Integer id) {
        CocheEntity vtc = cocheService.findById(id);
        return vtc != null
                ? ResponseEntity.ok(vtc)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea un nuevo coche en el sistema.
     *
     * @param vtc entidad del coche a crear
     * @return coche creado {@link CocheEntity}
     */
    @PostMapping
    public ResponseEntity<CocheEntity> create(@RequestBody CocheEntity vtc) {
        return ResponseEntity.ok(cocheService.save(vtc));
    }

    /**
     * Actualiza un coche existente.
     *
     * Si el coche no existe, devuelve una respuesta 404.
     *
     * @param id identificador del coche a actualizar
     * @param vtc datos actualizados del coche
     * @return coche actualizado {@link CocheEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<CocheEntity> update(@PathVariable Integer id,
                                              @RequestBody CocheEntity vtc) {

        if (cocheService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        // porque hereda de ServicioTuristicoEntity
        vtc.setIdServicio(id);

        return ResponseEntity.ok(cocheService.save(vtc));
    }

    /**
     * Elimina un coche por su identificador.
     *
     * Si el coche no existe, devuelve 404.
     *
     * @param id identificador del coche a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (cocheService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        cocheService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}