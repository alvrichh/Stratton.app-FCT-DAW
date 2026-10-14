package com.StrattonApp.Backend.DTO.request;

import java.time.LocalDate;

import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Estado;
import com.StrattonApp.Backend.entities.Suministro;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o actualizar un cliente.
 * <p>
 * Si se indica {@code cups} se crea (o actualiza) el suministro principal del cliente con la
 * comercializadora indicada en {@code companiaContratada} (o {@code comercializadoraId}).
 */
public class ClienteRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 60)
    private String nombre;

    @NotBlank(message = "Los apellidos del cliente son obligatorios")
    @Size(max = 80)
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = Cliente.DNI_REGEX, message = "El DNI/NIE no tiene un formato válido")
    private String dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = Cliente.TELEFONO_REGEX, message = "El teléfono debe tener 9 dígitos y empezar por 6, 7, 8 o 9")
    private String telefono;

    @Size(max = 150)
    private String direccion;

    @Pattern(regexp = Cliente.IBAN_REGEX, message = "El IBAN debe tener el formato ES00 0000 0000 0000 0000 0000")
    private String iban;

    private LocalDate fechaSubidaContrato;

    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;

    /** CUPS del suministro principal (opcional). */
    @Pattern(regexp = Suministro.CUPS_REGEX_INPUT, message = "El CUPS no tiene un formato válido (ej. ES0031408000000000AA0F)")
    private String cups;

    /** Nombre de la comercializadora (Endesa, Iberdrola, Naturgy...). */
    private String companiaContratada;

    private Long comercializadoraId;

    private Estado estado;

    private Double potencia;

    /** Empleado al que se asigna el cliente. Solo lo pueden fijar ADMIN/MANAGER. */
    private Long empleadoId;

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
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Double getPotencia() { return potencia; }
    public void setPotencia(Double potencia) { this.potencia = potencia; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
}
