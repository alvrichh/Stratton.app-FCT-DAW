package com.StrattonApp.Backend.DTO.response;

import java.util.List;

/**
 * Respuesta de autenticación: token JWT y datos básicos del empleado autenticado.
 */
public class JwtAuthenticationResponse {

    private String token;
    private String role;
    private List<String> roles;
    private Long id;
    private String username;
    private String nombre;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String token, String role, List<String> roles, Long id, String username, String nombre) {
        this.token = token;
        this.role = role;
        this.roles = roles;
        this.id = id;
        this.username = username;
        this.nombre = nombre;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
