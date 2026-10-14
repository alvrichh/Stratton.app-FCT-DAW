package com.StrattonApp.Backend.service;

import com.StrattonApp.Backend.DTO.request.SignUpRequest;
import com.StrattonApp.Backend.DTO.request.SigninRequest;
import com.StrattonApp.Backend.DTO.response.JwtAuthenticationResponse;

/**
 * Autenticación y registro de empleados.
 */
public interface AuthenticationService {

    /** Registra un nuevo empleado con rol USER y devuelve su token. */
    JwtAuthenticationResponse signup(SignUpRequest request);

    /** Inicia sesión y devuelve el token JWT con el rol del empleado. */
    JwtAuthenticationResponse signin(SigninRequest request);
}
