package com.StrattonApp.Backend.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interfaz para el servicio de manipulación de tokens JWT.
 */
public interface JwtService {

    /**
     * Extrae el nombre de usuario desde un token JWT.
     *
     * @param token Token JWT del cual extraer el nombre de usuario.
     * @return Nombre de usuario extraído del token.
     */
    String extractUserName(String token);

    /**
     * Genera un token JWT para un usuario.
     *
     * @param userDetails Detalles del usuario para los cuales se genera el token.
     * @return Token JWT generado.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Verifica si un token JWT es válido para un usuario.
     *
     * @param token       Token JWT a verificar.
     * @param userDetails Detalles del usuario para el cual se verifica el token.
     * @return `true` si el token es válido, `false` si no lo es.
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
