package com.StrattonApp.Backend.DTO.request;

import java.util.Set;

import com.StrattonApp.Backend.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o actualizar un empleado (solo ADMIN).
 * En una actualización la contraseña es opcional: si viene vacía se conserva la actual.
 */
public class EmpleadoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80)
    private String apellidos;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 100)
    private String email;

    /** Opcional al actualizar: vacía o ausente conserva la actual. Mínimo 6 caracteres si se envía. */
    private String password;

    private Set<Role> roles;

    private Long asesoriaId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre == null ? null : nombre.trim(); }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos == null ? null : apellidos.trim(); }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username == null ? null : username.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim().toLowerCase(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public Long getAsesoriaId() { return asesoriaId; }
    public void setAsesoriaId(Long asesoriaId) { this.asesoriaId = asesoriaId; }
}
