/*
 * Paquete Utils.
 */
package com.StrattonApp.Backend.utils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.exceptions.GlobalException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

/**
 * Clase utilitaria para operaciones relacionadas con tokens JWT.
 */
public class TokenUtils {
    
    // Clave secreta para firmar y verificar tokens JWT
    private final static String ACCESS_TOKEN_SECRET = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";

    // Tiempo de validez del token en segundos (3 horas)
    private final static Long ACCESS_TOKEN_VALIDATY_SECONDS = (long) 3 * 60 * 60;

    /**
     * Genera un token JWT con la información proporcionada.
     *
     * @param username Nombre de usuario (email)
     * @param name     Nombre del usuario
     * @param rol      Rol del usuario
     * @param id       ID del usuario
     * @return Token JWT generado, incluido el prefijo "Bearer "
     */
    public static String generateToken(String username, String name, Role rol, Integer id) {

        // Establecemos la fecha de expiración del token en milisegundos
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDATY_SECONDS * 1000);

        // Creamos un mapa para guardar la información adicional en el token
        Map<String, Object> extra = new HashMap<>();
        extra.put("name", name);
        extra.put("rol", rol.name()); // Guardamos el nombre del rol
        extra.put("id", id);

        // Construimos el token JWT
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .claims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();

        return "Bearer " + token;
    }

    /**
     * Obtiene todos los claims (payload) de un token JWT.
     *
     * @param token Token JWT
     * @return Claims extraídos del token
     * @throws JwtException                Si ocurre un error al parsear o verificar el token
     * @throws IllegalArgumentException    Si el token es inválido
     * @throws NoSuchAlgorithmException    Si el algoritmo de firma no es válido
     */
    public static Claims getAllClaimsFromToken(String token)
            throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;
    }

    /**
     * Obtiene la autenticación a partir de un token JWT.
     *
     * @param token Token JWT
     * @return Objeto UsernamePasswordAuthenticationToken
     * @throws JwtException                Si ocurre un error al parsear o verificar el token
     * @throws IllegalArgumentException    Si el token es inválido
     * @throws NoSuchAlgorithmException    Si el algoritmo de firma no es válido
     * @throws GlobalException              Si el formato del token no es válido o ocurre un error específico
     */
    public static UsernamePasswordAuthenticationToken getAuthentication(String token)
            throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, GlobalException {
        Claims claims;

        if (!token.startsWith("Bearer ")) {
            throw new GlobalException("Formato de token no válido");
        }
        token = token.substring(7);
        try {
            // Obtenemos los claims (payload) del token
            claims = getAllClaimsFromToken(token);
        } catch (IllegalArgumentException e) {
            throw new GlobalException("Imposible encontrar un JWT Token");
        } catch (ExpiredJwtException e) {
            throw new GlobalException("Token expirado");
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("Algoritmo no válido");
        } catch (MalformedJwtException e) {
            throw new GlobalException("Token malformado");
        }

        // Extraemos el nombre de usuario y el rol del token
        String username = claims.getSubject();
        String rol = (String) claims.get("rol");
        Role roleEnum = Role.valueOf(rol); // Convertimos el string del rol a un enum Role
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleEnum.name()));

        // Devolvemos el objeto de autenticación
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * Verifica si el usuario tiene el rol de administrador basado en el token JWT.
     *
     * @param token El token JWT del usuario.
     * @return true si el usuario tiene el rol de administrador, false de lo contrario.
     */
    public static boolean isAdmin(String token) {
        String rol = extractUserRole(token);
        return Role.ADMIN.name().equals(rol);
    }

	private static String extractUserRole(String token) {
		return extractUserRole(token);
	}
}
