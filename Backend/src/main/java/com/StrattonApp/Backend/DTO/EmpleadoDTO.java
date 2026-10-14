package com.StrattonApp.Backend.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;

/**
 * Representación de un empleado para el frontend (nunca incluye la contraseña).
 */
public class EmpleadoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String username;
    /** Rol principal (ADMIN, MANAGER o USER). */
    private String role;
    private List<String> roles;
    private Long asesoriaId;
    private String asesoria;
    private int totalClientes;
    private List<ClienteDTO> clientes;

    public EmpleadoDTO() {
    }

    /** Convierte un empleado, incluyendo (o no) el detalle de sus clientes. */
    public static EmpleadoDTO from(Empleado e, boolean conClientes) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.id = e.getId();
        dto.nombre = e.getNombre();
        dto.apellidos = e.getApellidos();
        dto.email = e.getEmail();
        dto.username = e.getUsername();
        dto.role = e.getMainRole();
        dto.roles = e.getRoles().stream().map(Role::name).sorted().collect(Collectors.toList());
        if (e.getAsesoria() != null) {
            dto.asesoriaId = e.getAsesoria().getIdAsesoria();
            dto.asesoria = e.getAsesoria().getNombre();
        }
        dto.totalClientes = e.getClientes() == null ? 0 : e.getClientes().size();
        if (conClientes && e.getClientes() != null) {
            dto.clientes = e.getClientes().stream().map(ClienteDTO::from).collect(Collectors.toList());
        } else {
            dto.clientes = List.of();
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Long getAsesoriaId() { return asesoriaId; }
    public void setAsesoriaId(Long asesoriaId) { this.asesoriaId = asesoriaId; }
    public String getAsesoria() { return asesoria; }
    public void setAsesoria(String asesoria) { this.asesoria = asesoria; }
    public int getTotalClientes() { return totalClientes; }
    public void setTotalClientes(int totalClientes) { this.totalClientes = totalClientes; }
    public List<ClienteDTO> getClientes() { return clientes; }
    public void setClientes(List<ClienteDTO> clientes) { this.clientes = clientes; }
}
