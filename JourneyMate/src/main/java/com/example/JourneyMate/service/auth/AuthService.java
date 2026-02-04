package com.example.JourneyMate.service.auth;

import com.example.JourneyMate.config.security.JWTUtil;
import com.example.JourneyMate.dao.user.RolRepository;
import com.example.JourneyMate.dao.user.TokenJWTRepository;
import com.example.JourneyMate.dao.user.UsuarioRepository;
import com.example.JourneyMate.dto.auth.LoginRequest;
import com.example.JourneyMate.dto.auth.RegisterRequest;
import com.example.JourneyMate.dto.auth.AuthResponse;
import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.entity.user.TokenJWTEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TokenJWTRepository tokenJWTRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       TokenJWTRepository tokenJWTRepository,
                       PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.tokenJWTRepository = tokenJWTRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // REGISTRO
    public AuthResponse register(RegisterRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        RolEntity rol = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario);

        TokenJWTEntity tokenEntity = new TokenJWTEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUsuario(usuario);
        tokenJWTRepository.save(tokenEntity);

        return new AuthResponse(token);
    }

    // LOGIN
    public AuthResponse login(LoginRequest request) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getContrasenia())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(usuario);

        TokenJWTEntity tokenEntity = new TokenJWTEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUsuario(usuario);
        tokenJWTRepository.save(tokenEntity);

        return new AuthResponse(token);
    }
}
