package com.StrattonApp.Backend.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts.SIG;

/**
 * Implementación de {@link JwtService} con jjwt 0.12 (HMAC-SHA256).
 * El token incluye los claims {@code id}, {@code role} (rol principal), {@code roles} y {@code nombre}.
 */
@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtServiceImpl(@Value("${jwt.secret:}") String secret,
            @Value("${jwt.expiration-ms:36000000}") long expirationMs) {
        this.signingKey = resolverClave(secret);
        this.expirationMs = expirationMs;
    }

    /**
     * Usa la clave configurada (base64, mínimo 32 bytes) o, si no hay ninguna, genera una aleatoria
     * y avisa: los tokens emitidos dejarán de ser válidos al reiniciar la aplicación.
     */
    private static SecretKey resolverClave(String secret) {
        if (secret == null || secret.isBlank()) {
            log.warn("JWT_SECRET no definido: se usa una clave aleatoria. Los tokens no sobrevivirán a un reinicio. "
                    + "Define JWT_SECRET (openssl rand -base64 32) en producción.");
            return SIG.HS256.key().build();
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret.trim());
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT_SECRET debe estar codificado en base64 (openssl rand -base64 32)", e);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET es demasiado corto: se necesitan al menos 32 bytes (256 bits)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new LinkedHashMap<>();
        if (userDetails instanceof Empleado empleado) {
            extraClaims.put("id", empleado.getId());
            extraClaims.put("role", empleado.getMainRole());
            extraClaims.put("roles", empleado.getRoles().stream().map(Role::name).sorted().collect(Collectors.toList()));
            extraClaims.put("nombre", empleado.getNombreCompleto());
        }
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName != null && userName.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        return exp != null && exp.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
