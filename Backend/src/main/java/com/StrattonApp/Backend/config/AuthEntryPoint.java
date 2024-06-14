package com.StrattonApp.Backend.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Esta clase maneja los intentos de acceso no autorizados a la aplicación implementando
 * la interfaz AuthenticationEntryPoint. Envía una respuesta JSON con el mensaje de error
 * y el código de estado correspondiente.
 */
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Maneja el comienzo de un esquema de autenticación.
     * <p>
     * Este método se activa cada vez que un usuario no autenticado intenta acceder a un recurso
     * seguro. Envía una respuesta 401 Unauthorized junto con un cuerpo JSON que contiene los
     * detalles del error.
     * </p>
     * 
     * @param request       el objeto HttpServletRequest que contiene la solicitud que el cliente
     *                      hizo al servlet
     * @param response      el objeto HttpServletResponse que contiene la respuesta que el servlet
     *                      devuelve al cliente
     * @param authException la excepción que se lanza cuando ocurre un error de autenticación
     * 
     * @throws IOException              si ocurre una excepción de entrada o salida
     * @throws ServletException         si ocurre una excepción específica del servlet
     * @throws StreamWriteException     si ocurre un error al escribir JSON en la respuesta
     * @throws DatabindException        si ocurre un error al vincular JSON
     * @throws java.io.IOException      si ocurre una excepción de entrada o salida
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException, StreamWriteException, DatabindException, java.io.IOException {

        response.addHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
