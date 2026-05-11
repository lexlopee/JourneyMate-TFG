package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.CruceroEntity;
import com.example.JourneyMate.service.service_type.CruceroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los cruceros del sistema.
 *
 * Permite listar, consultar, crear, actualizar y eliminar cruceros
 * que heredan de {@link com.example.JourneyMate.entity.service.ServicioTuristicoEntity}.
 */
@RestController
@RequestMapping("/cruceros")
public class CruceroController {

    @Autowired
    private CruceroService cruceroService;

    /**
     * Obtiene la lista de todos los cruceros disponibles.
     *
     * @return lista de {@link CruceroEntity}
     */
    @GetMapping
    public ResponseEntity<List<CruceroEntity>> findAll() {
        return ResponseEntity.ok(cruceroService.findAll());
    }

    /**
     * Obtiene un crucero por su identificador.
     *
     * @param id identificador del crucero
     * @return entidad {@link CruceroEntity} si existe,
     *         o respuesta 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<CruceroEntity> findById(@PathVariable Integer id) {
        CruceroEntity crucero = cruceroService.findByIdCrucero(id);
        return crucero != null
                ? ResponseEntity.ok(crucero)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea un nuevo crucero en el sistema.
     *
     * @param crucero entidad del crucero a crear
     * @return crucero creado {@link CruceroEntity}
     */
    @PostMapping
    public ResponseEntity<CruceroEntity> create(@RequestBody CruceroEntity crucero) {
        return ResponseEntity.ok(cruceroService.save(crucero));
    }

    /**
     * Actualiza un crucero existente.
     *
     * Si el crucero no existe, devuelve una respuesta 404.
     *
     * @param id identificador del crucero a actualizar
     * @param crucero datos actualizados del crucero
     * @return crucero actualizado {@link CruceroEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<CruceroEntity> update(@PathVariable Integer id,
                                                @RequestBody CruceroEntity crucero) {

        if (cruceroService.findByIdCrucero(id) == null) {
            return ResponseEntity.notFound().build();
        }

        // porque hereda de ServicioTuristicoEntity
        crucero.setIdServicio(id);

        return ResponseEntity.ok(cruceroService.save(crucero));
    }

    /**
     * Elimina un crucero por su identificador.
     *
     * Si el crucero no existe, devuelve 404.
     *
     * @param id identificador del crucero a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (cruceroService.findByIdCrucero(id) == null) {
            return ResponseEntity.notFound().build();
        }

        cruceroService.deleteByIdCrucero(id);
        return ResponseEntity.noContent().build();
    }
}