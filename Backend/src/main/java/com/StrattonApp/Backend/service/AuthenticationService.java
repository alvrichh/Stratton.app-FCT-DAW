package com.StrattonApp.Backend.service;

import com.StrattonApp.Backend.DTO.response.JwtAuthenticationResponse;
import com.StrattonApp.Backend.DTO.resquest.SignUpRequest;
import com.StrattonApp.Backend.DTO.resquest.SigninRequest;

/**
 * Interfaz que define los métodos para el servicio de autenticación y autorización.
 */
public interface AuthenticationService {

    /**
     * Realiza el registro de un nuevo usuario y proporciona un token JWT en respuesta.
     *
     * @param request Datos de registro del usuario.
     * @return Respuesta de autenticación que incluye el token JWT.
     */
    JwtAuthenticationResponse signup(SignUpRequest request);

    /**
     * Realiza el inicio de sesión y proporciona un token JWT en respuesta.
     *
     * @param request Datos de inicio de sesión del usuario.
     * @return Respuesta de autenticación que incluye el token JWT.
     */
    JwtAuthenticationResponse signin(SigninRequest request);
}