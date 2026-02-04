package com.example.JourneyMate.controller.auth;

import com.example.JourneyMate.dto.auth.LoginRequest;
import com.example.JourneyMate.dto.auth.RegisterRequest;
import com.example.JourneyMate.dto.auth.AuthResponse;
import com.example.JourneyMate.service.auth.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
