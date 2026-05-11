package com.example.JourneyMate.controller.auth;

import com.example.JourneyMate.dto.auth.AuthResponse;
import com.example.JourneyMate.dto.auth.LoginRequest;
import com.example.JourneyMate.dto.auth.RegisterRequest;
import com.example.JourneyMate.service.auth.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador encargado de gestionar la autenticación de usuarios.
 * <p>
 * Proporciona endpoints para el registro e inicio de sesión
 * dentro de la aplicación JourneyMate.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Servicio encargado de la lógica de autenticación.
     */
    private final AuthService authService;

    /**
     * Constructor del controlador de autenticación.
     *
     * @param authService servicio de autenticación utilizado
     *                    para registrar e iniciar sesión de usuarios
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request datos necesarios para el registro del usuario
     * @return respuesta con la información de autenticación generada
     */
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Autentica un usuario existente.
     *
     * @param request credenciales de inicio de sesión
     * @return respuesta con la información de autenticación del usuario
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}