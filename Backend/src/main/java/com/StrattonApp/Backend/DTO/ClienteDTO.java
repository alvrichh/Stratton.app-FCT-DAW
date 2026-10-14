package com.StrattonApp.Backend.DTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Suministro;

/**
 * Representación de un cliente para el frontend, con su suministro principal y su asesor.
 */
public class ClienteDTO {

    private Long idCliente;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private String iban;
    private LocalDate fechaSubidaContrato;
    private LocalDate fechaNacimiento;
    /** CUPS del suministro principal (el primero), o null si no tiene. */
    private String cups;
    private String companiaContratada;
    private Long comercializadoraId;
    private String logo;
    private Long empleadoId;
    private String empleadoNombre;
    private Long asesoriaId;
    private String asesoriaNombre;
    private List<SuministroDTO> suministros;

    public ClienteDTO() {
    }

    public static ClienteDTO from(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.idCliente = c.getIdCliente();
        dto.nombre = c.getNombre();
        dto.apellidos = c.getApellidos();
        dto.dni = c.getDni();
        dto.email = c.getEmail();
        dto.telefono = c.getTelefono();
        dto.direccion = c.getDireccion();
        dto.iban = c.getIban();
        dto.fechaSubidaContrato = c.getFechaSubidaContrato();
        dto.fechaNacimiento = c.getFechaNacimiento();
        if (c.getComercializadora() != null) {
            dto.comercializadoraId = c.getComercializadora().getIdComercializadora();
            dto.companiaContratada = c.getComercializadora().getCompaniaContratada();
            dto.logo = c.getComercializadora().getLogo();
        }
        if (c.getEmpleado() != null) {
            dto.empleadoId = c.getEmpleado().getId();
            dto.empleadoNombre = c.getEmpleado().getNombreCompleto();
        }
        if (c.getAsesoria() != null) {
            dto.asesoriaId = c.getAsesoria().getIdAsesoria();
            dto.asesoriaNombre = c.getAsesoria().getNombre();
        }
        List<Suministro> sums = c.getSuministros();
        if (sums != null && !sums.isEmpty()) {
            dto.cups = sums.get(0).getCups();
            dto.suministros = sums.stream().map(SuministroDTO::from).collect(Collectors.toList());
        } else {
            dto.suministros = List.of();
        }
        return dto;
    }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public LocalDate getFechaSubidaContrato() { return fechaSubidaContrato; }
    public void setFechaSubidaContrato(LocalDate fechaSubidaContrato) { this.fechaSubidaContrato = fechaSubidaContrato; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getCups() { return cups; }
    public void setCups(String cups) { this.cups = cups; }
    public String getCompaniaContratada() { return companiaContratada; }
    public void setCompaniaContratada(String companiaContratada) { this.companiaContratada = companiaContratada; }
    public Long getComercializadoraId() { return comercializadoraId; }
    public void setComercializadoraId(Long comercializadoraId) { this.comercializadoraId = comercializadoraId; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public String getEmpleadoNombre() { return empleadoNombre; }
    public void setEmpleadoNombre(String empleadoNombre) { this.empleadoNombre = empleadoNombre; }
    public Long getAsesoriaId() { return asesoriaId; }
    public void setAsesoriaId(Long asesoriaId) { this.asesoriaId = asesoriaId; }
    public String getAsesoriaNombre() { return asesoriaNombre; }
    public void setAsesoriaNombre(String asesoriaNombre) { this.asesoriaNombre = asesoriaNombre; }
    public List<SuministroDTO> getSuministros() { return suministros; }
    public void setSuministros(List<SuministroDTO> suministros) { this.suministros = suministros; }
}
