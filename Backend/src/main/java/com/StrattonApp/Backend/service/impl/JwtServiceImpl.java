package com.StrattonApp.Backend.service.impl;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Implementación del servicio JwtService para manejar tokens JWT.
 */
@Service
public class JwtServiceImpl implements JwtService {

    // Llave para firmar el JWT, obtenida del archivo de propiedades de la aplicación.
    @Value("${jwt.secret}")
    private String jwtSigningKey;

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token Token JWT.
     * @return Nombre de usuario extraído del token.
     */
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param userDetails Detalles del usuario.
     * @return Token JWT generado.
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Verifica si el token JWT es válido para el usuario proporcionado.
     *
     * @param token       Token JWT.
     * @param userDetails Detalles del usuario.
     * @return `true` si el token es válido, `false` si no lo es.
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Método genérico para extraer información del token JWT.
    // Los claims son declaraciones que contienen información sobre el usuario y metadatos adicionales.
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    // Genera el token JWT incluyendo los claims adicionales y los detalles del usuario.
    // Los claims adicionales pueden ser usados para almacenar información adicional sobre el usuario o el token.
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token expira en 10 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Verifica si el token ha expirado.
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrae la fecha de expiración del token.
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrae todas las claims del token.
    // Aquí se extrae y se procesa el conjunto completo de claims del JWT.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Obtiene la llave de firma a partir de la cadena codificada en base64.
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
