package com.StrattonApp.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.StrattonApp.Backend.DTO.response.JwtAuthenticationResponse;
import com.StrattonApp.Backend.DTO.resquest.SignUpRequest;
import com.StrattonApp.Backend.DTO.resquest.SigninRequest;
import com.StrattonApp.Backend.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar las operaciones de autenticación de empleados.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:80")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Registra un nuevo empleado.
     *
     * @param request Datos de registro del empleado.
     * @return ResponseEntity con la respuesta de autenticación JWT.
     */
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    /**
     * Inicia sesión con credenciales de empleado.
     *
     * @param request Datos de inicio de sesión.
     * @return ResponseEntity con la respuesta de autenticación JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.signin(request));
    }
}