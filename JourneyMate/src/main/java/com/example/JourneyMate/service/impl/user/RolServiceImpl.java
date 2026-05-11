package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.dao.user.RolRepository;
import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.service.user.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de roles.
 * Proporciona operaciones de consulta, persistencia y gestión de relaciones
 * entre usuarios y roles.
 */
@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    /**
     * Obtiene todos los roles registrados.
     *
     * @return lista de roles
     */
    @Override
    public List<RolEntity> findAll() {
        return rolRepository.findAll();
    }

    /**
     * Busca un rol por su identificador.
     *
     * @param idRol identificador del rol
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public RolEntity findById(Integer idRol) {
        return rolRepository.findById(idRol).orElse(null);
    }

    /**
     * Busca un rol por su nombre.
     *
     * @param rolName nombre del rol
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public RolEntity findByRolName(String rolName) {
        return rolRepository.findByNombre(rolName).orElse(null);
    }

    /**
     * Obtiene una lista con un rol filtrado por nombre.
     * Devuelve lista vacía si no existe.
     *
     * @param rolName nombre del rol
     * @return lista con el rol encontrado o vacía
     */
    @Override
    public List<RolEntity> findAllByRolName(String rolName) {
        return rolRepository.findByNombre(rolName)
                .map(List::of)
                .orElse(List.of());
    }

    /**
     * Guarda o actualiza un rol.
     *
     * @param rolEntity entidad de rol
     * @return entidad persistida
     */
    @Override
    public RolEntity save(RolEntity rolEntity) {
        return rolRepository.save(rolEntity);
    }

    /**
     * Obtiene todos los roles asociados a un usuario.
     *
     * @param idUser identificador del usuario
     * @return lista de roles del usuario
     */
    @Override
    public List<RolEntity> findAllByUserId(Integer idUser) {
        return rolRepository.findByUsuarios_IdUsuario(idUser);
    }

    /**
     * Elimina la relación entre un usuario y todos sus roles.
     * No elimina los roles, solo la asociación con el usuario.
     *
     * @param idUser identificador del usuario
     */
    @Override
    public void deleteAllByUserId(Integer idUser) {
        var roles = rolRepository.findByUsuarios_IdUsuario(idUser);

        roles.forEach(rol -> {
            rol.getUsuarios().removeIf(u -> u.getIdUsuario().equals(idUser));
            rolRepository.save(rol);
        });
    }

    /**
     * Elimina un rol por su identificador.
     *
     * @param idRol identificador del rol a eliminar
     */
    @Override
    public void deleteAllByRolId(Integer idRol) {
        rolRepository.deleteById(idRol);
    }
}