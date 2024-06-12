package com.StrattonApp.Backend.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.StrattonApp.Backend.utils.TokenUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null) {
            try {
                // Obtener la autenticación del token
                UsernamePasswordAuthenticationToken authenticationToken = TokenUtils.getAuthentication(requestTokenHeader);

                // Configurar los detalles de autenticación
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                logger.error("Error al procesar el token de autorización: {}", e.getMessage());
            }
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}
