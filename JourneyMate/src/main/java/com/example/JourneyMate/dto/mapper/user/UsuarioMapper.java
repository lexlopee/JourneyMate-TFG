package com.example.JourneyMate.dto.mapper.user;

import com.example.JourneyMate.dto.usuario.UsuarioRequestDTO;
import com.example.JourneyMate.dto.usuario.UsuarioResponseDTO;
import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;

public class UsuarioMapper {

    public static UsuarioEntity toEntity(UsuarioRequestDTO dto) {
        UsuarioEntity entity = new UsuarioEntity();

        entity.setTelefono(dto.getTelefono());
        entity.setContrasenia(dto.getContrasenia());
        entity.setNombre(dto.getNombre());
        entity.setPrimerApellido(dto.getPrimerApellido());
        entity.setSegundoApellido(dto.getSegundoApellido());
        entity.setFechaRegistro(dto.getFechaRegistro());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setEmail(dto.getEmail());

        // Relación con Rol
        RolEntity rol = new RolEntity();
        rol.setIdRol(dto.getIdRol());
        entity.setRol(rol);

        return entity;
    }

    public static UsuarioResponseDTO toDTO(UsuarioEntity entity) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setIdUsuario(entity.getIdUsuario());
        dto.setTelefono(entity.getTelefono());
        dto.setNombre(entity.getNombre());
        dto.setPrimerApellido(entity.getPrimerApellido());
        dto.setSegundoApellido(entity.getSegundoApellido());
        dto.setFechaRegistro(entity.getFechaRegistro());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setEmail(entity.getEmail());
        dto.setIdRol(entity.getRol().getIdRol());

        return dto;
    }
}
