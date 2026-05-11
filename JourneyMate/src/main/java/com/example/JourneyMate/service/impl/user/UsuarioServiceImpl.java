package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.dao.user.UsuarioRepository;
import com.example.JourneyMate.entity.user.UsuarioEntity;
import com.example.JourneyMate.service.user.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de usuarios.
 * Proporciona operaciones de consulta, validación, persistencia y eliminación
 * de usuarios en el sistema.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    @Override
    public Optional<UsuarioEntity> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Verifica si existe un usuario con el email indicado.
     *
     * @param email correo electrónico a verificar
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    @Override
    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public UsuarioEntity findById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    /**
     * Guarda o actualiza un usuario.
     *
     * @param usuarioEntity entidad de usuario
     * @return usuario persistido
     */
    @Override
    public UsuarioEntity save(UsuarioEntity usuarioEntity) {
        return usuarioRepository.save(usuarioEntity);
    }

    /**
     * Elimina un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario a eliminar
     */
    @Override
    public void deleteById(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }
}