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
import com.StrattonApp.Backend.entities.Empleado;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


/**
 * Implementación de {@link JwtService} para la generación, validación y extracción de tokens JWT.
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSigningKey;


    /**
     * Extrae el nombre de usuario desde el token JWT.
     *
     * @param token Token JWT
     * @return Nombre de usuario extraído del token
     */
    
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Extrae el rol del usuario desde el token JWT.
     *
     * @param token Token JWT
     * @return Rol del usuario extraído del token
     */
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    /*@Override
    public String extractPassword(String token) {
        return extractClaim(token, claims -> claims.get("password", String.class));
    }*/

    /**
     * Genera un token JWT basado en los detalles del usuario.
     *
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", ((Empleado) userDetails).getMainRole()); // Añadir el rol a los claims
        return generateToken(extraClaims, userDetails);
    }

    /**
     * Valida si un token JWT es válido para el usuario dado.
     *
     * @param token       Token JWT
     * @param userDetails Detalles del usuario
     * @return `true` si el token es válido, `false` si no
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Método genérico para extraer un claim del token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Genera un token JWT con los claims especificados y los detalles del usuario
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token expira en 10 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Valida si el token JWT ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrae la fecha de expiración del token JWT
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrae todos los claims del token JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Obtiene la clave de firma a partir de la clave secreta configurada
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
}