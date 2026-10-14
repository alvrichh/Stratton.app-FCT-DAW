package com.StrattonApp.Backend.DTO;

import com.StrattonApp.Backend.entities.Comercializadora;

/**
 * Representación de una comercializadora para el frontend.
 */
public class ComercializadoraDTO {

    private Long idComercializadora;
    private String companiaContratada;
    private String plan;
    private String logo;

    public ComercializadoraDTO() {
    }

    public static ComercializadoraDTO from(Comercializadora c) {
        ComercializadoraDTO dto = new ComercializadoraDTO();
        dto.idComercializadora = c.getIdComercializadora();
        dto.companiaContratada = c.getCompaniaContratada();
        dto.plan = c.getPlan();
        dto.logo = c.getLogo();
        return dto;
    }

    public Long getIdComercializadora() { return idComercializadora; }
    public void setIdComercializadora(Long idComercializadora) { this.idComercializadora = idComercializadora; }
    public String getCompaniaContratada() { return companiaContratada; }
    public void setCompaniaContratada(String companiaContratada) { this.companiaContratada = companiaContratada; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
}
