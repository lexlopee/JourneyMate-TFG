package com.example.JourneyMate.service.auth;

import com.example.JourneyMate.config.security.JWTUtil;
import com.example.JourneyMate.dao.user.RolRepository;
import com.example.JourneyMate.dao.user.TokenJWTRepository;
import com.example.JourneyMate.dao.user.UsuarioRepository;
import com.example.JourneyMate.dto.auth.AuthResponse;
import com.example.JourneyMate.dto.auth.LoginRequest;
import com.example.JourneyMate.dto.auth.RegisterRequest;
import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.entity.user.TokenJWTEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Servicio encargado de la autenticación y registro de usuarios.
 *
 * Gestiona el proceso de registro, login, generación de JWT
 * y almacenamiento de tokens en base de datos.
 */
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

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * - Verifica que el email no esté registrado
     * - Asigna el rol por defecto "USER"
     * - Encripta la contraseña
     * - Genera un token JWT
     * - Guarda el token en base de datos
     *
     * @param request datos de registro del usuario
     * @return respuesta de autenticación con token y datos básicos del usuario
     * @throws RuntimeException si el email ya está registrado o el rol no existe
     */
    public AuthResponse register(RegisterRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        RolEntity rol = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.getNombre());
        usuario.setPrimerApellido(request.getPrimerApellido());
        usuario.setSegundoApellido(request.getSegundoApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setRol(rol);

        usuario = usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario);

        TokenJWTEntity tokenEntity = new TokenJWTEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUsuario(usuario);
        tokenEntity.setFechaCreacion(LocalDateTime.now());
        tokenEntity.setFechaExpiacion(LocalDateTime.now().plusDays(7));
        tokenJWTRepository.save(tokenEntity);

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getNombre());
    }

    /**
     * Autentica un usuario en el sistema.
     *
     * - Verifica credenciales
     * - Genera token JWT
     * - Guarda el token en base de datos
     *
     * @param request datos de login (email y contraseña)
     * @return respuesta de autenticación con token y datos básicos del usuario
     * @throws RuntimeException si las credenciales son inválidas
     */
    public AuthResponse login(LoginRequest request) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(usuario);

        TokenJWTEntity tokenEntity = new TokenJWTEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUsuario(usuario);
        tokenEntity.setFechaCreacion(LocalDateTime.now());
        tokenEntity.setFechaExpiacion(LocalDateTime.now().plusDays(7));
        tokenJWTRepository.save(tokenEntity);

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getNombre());
    }
}