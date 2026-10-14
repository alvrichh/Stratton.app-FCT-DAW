package com.StrattonApp.Backend.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Generación y validación de tokens JWT.
 */
public interface JwtService {

    /** Extrae el nombre de usuario (subject) del token. */
    String extractUserName(String token);

    /** Genera un token para el usuario con sus roles e id como claims. */
    String generateToken(UserDetails userDetails);

    /** true si el token pertenece al usuario y no ha expirado. */
    boolean isTokenValid(String token, UserDetails userDetails);
}
