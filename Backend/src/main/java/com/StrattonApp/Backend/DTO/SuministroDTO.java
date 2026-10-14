package com.StrattonApp.Backend.DTO;

import com.StrattonApp.Backend.entities.Estado;
import com.StrattonApp.Backend.entities.Suministro;

/**
 * Representación de un suministro para el frontend.
 */
public class SuministroDTO {

    private String cups;
    private String direccion;
    private Estado estado;
    private String mensaje;
    private Double potencia;
    private Long clienteId;
    private String clienteNombre;
    private Long comercializadoraId;
    private String companiaContratada;
    private String logo;

    public SuministroDTO() {
    }

    public static SuministroDTO from(Suministro s) {
        SuministroDTO dto = new SuministroDTO();
        dto.cups = s.getCups();
        dto.direccion = s.getDireccion();
        dto.estado = s.getEstado();
        dto.mensaje = s.getMensaje();
        dto.potencia = s.getPotencia();
        if (s.getCliente() != null) {
            dto.clienteId = s.getCliente().getIdCliente();
            dto.clienteNombre = s.getCliente().getNombre() + " " + s.getCliente().getApellidos();
        }
        if (s.getComercializadora() != null) {
            dto.comercializadoraId = s.getComercializadora().getIdComercializadora();
            dto.companiaContratada = s.getComercializadora().getCompaniaContratada();
            dto.logo = s.getComercializadora().getLogo();
        }
        return dto;
    }

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
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Long getComercializadoraId() { return comercializadoraId; }
    public void setComercializadoraId(Long comercializadoraId) { this.comercializadoraId = comercializadoraId; }
    public String getCompaniaContratada() { return companiaContratada; }
    public void setCompaniaContratada(String companiaContratada) { this.companiaContratada = companiaContratada; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
}
