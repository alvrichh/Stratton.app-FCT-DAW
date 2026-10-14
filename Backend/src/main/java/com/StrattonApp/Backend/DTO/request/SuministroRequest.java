package com.StrattonApp.Backend.DTO.request;

import com.StrattonApp.Backend.entities.Estado;
import com.StrattonApp.Backend.entities.Suministro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o actualizar un suministro de un cliente.
 */
public class SuministroRequest {

    @NotBlank(message = "El CUPS es obligatorio")
    @Pattern(regexp = Suministro.CUPS_REGEX_INPUT, message = "El CUPS no tiene un formato válido (ej. ES0031408000000000AA0F)")
    private String cups;

    @NotBlank(message = "La dirección del suministro es obligatoria")
    @Size(max = 150)
    private String direccion;

    private Estado estado;

    @Size(max = 255)
    private String mensaje;

    private Double potencia;

    private String companiaContratada;

    private Long comercializadoraId;

    public String getCups() { return cups; }
    public void setCups(String cups) { this.cups = cups; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Double getPotencia() { return potencia; }
    public void setPotencia(Double potencia) { this.potencia = potencia; }
    public String getCompaniaContratada() { return companiaContratada; }
    public void setCompaniaContratada(String companiaContratada) { this.companiaContratada = companiaContratada; }
    public Long getComercializadoraId() { return comercializadoraId; }
    public void setComercializadoraId(Long comercializadoraId) { this.comercializadoraId = comercializadoraId; }
}
