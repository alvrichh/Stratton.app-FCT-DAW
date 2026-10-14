package com.StrattonApp.Backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.request.SignUpRequest;
import com.StrattonApp.Backend.DTO.request.SigninRequest;
import com.StrattonApp.Backend.DTO.response.JwtAuthenticationResponse;
import com.StrattonApp.Backend.exceptions.ForbiddenException;
import com.StrattonApp.Backend.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Autenticación: login y registro de empleados.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /** Si es false, solo un ADMIN puede crear empleados (POST /api/v2/empleados). */
    @Value("${app.registro-abierto:true}")
    private boolean registroAbierto;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Registra un nuevo empleado (rol USER) y devuelve su token. Desactivable con REGISTRO_ABIERTO=false")
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> signup(@Valid @RequestBody SignUpRequest request) {
        if (!registroAbierto) {
            throw new ForbiddenException("El registro público está desactivado: pide a un administrador que cree tu usuario");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signup(request));
    }

    @Operation(summary = "Inicia sesión y devuelve el token JWT")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> signin(@Valid @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.signin(request));
    }
}
