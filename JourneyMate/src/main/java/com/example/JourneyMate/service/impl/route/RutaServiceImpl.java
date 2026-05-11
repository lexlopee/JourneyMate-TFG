package com.example.JourneyMate.service.impl.route;

import com.example.JourneyMate.dao.route.RutaRepository;
import com.example.JourneyMate.entity.route.RutaEntity;
import com.example.JourneyMate.service.route.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de rutas.
 * Proporciona operaciones CRUD y consultas relacionadas con las rutas de los usuarios.
 */
@Service
public class RutaServiceImpl implements RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    /**
     * Obtiene todas las rutas asociadas a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de rutas del usuario
     */
    @Override
    public List<RutaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return rutaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    /**
     * Obtiene todas las rutas registradas.
     *
     * @return lista de todas las rutas
     */
    @Override
    public List<RutaEntity> findAll() {
        return rutaRepository.findAll();
    }

    /**
     * Busca una ruta por su identificador.
     *
     * @param idRuta identificador de la ruta
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public RutaEntity findById(Integer idRuta) {
        return rutaRepository.findById(idRuta).orElse(null);
    }

    /**
     * Guarda o actualiza una ruta.
     *
     * @param ruta entidad de ruta a persistir
     * @return la entidad guardada
     */
    @Override
    public RutaEntity saveRuta(RutaEntity ruta) {
        return rutaRepository.save(ruta);
    }

    /**
     * Elimina una ruta por su identificador.
     *
     * @param idRuta identificador de la ruta a eliminar
     */
    @Override
    public void deleteBy(Integer idRuta) {
        rutaRepository.deleteById(idRuta);
    }
}