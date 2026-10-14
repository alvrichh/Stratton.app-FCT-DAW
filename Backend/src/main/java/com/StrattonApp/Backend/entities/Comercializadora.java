package com.StrattonApp.Backend.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Compañía comercializadora de energía (Endesa, Iberdrola, Naturgy...).
 */
@Entity
@Table(name = "comercializadoras")
public class Comercializadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComercializadora;

    @NotBlank(message = "El nombre de la comercializadora no puede estar vacío.")
    @Column(nullable = false, unique = true, length = 60)
    private String companiaContratada;

    @Column(length = 100)
    private String plan;

    /** Ruta del logotipo que muestra el frontend (relativa a assets/). */
    @Column(length = 150)
    private String logo;

    @OneToMany(mappedBy = "comercializadora")
    @JsonIgnore
    private List<Suministro> suministros = new ArrayList<>();

    @OneToMany(mappedBy = "comercializadora")
    @JsonIgnore
    private List<Cliente> clientes = new ArrayList<>();

    public Comercializadora() {
    }

    public Comercializadora(String companiaContratada, String plan, String logo) {
        this.companiaContratada = companiaContratada;
        this.plan = plan;
        this.logo = logo;
    }

    public Long getIdComercializadora() { return idComercializadora; }
    public void setIdComercializadora(Long idComercializadora) { this.idComercializadora = idComercializadora; }
    public String getCompaniaContratada() { return companiaContratada; }
    public void setCompaniaContratada(String companiaContratada) { this.companiaContratada = companiaContratada; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public List<Suministro> getSuministros() { return suministros; }
    public void setSuministros(List<Suministro> suministros) { this.suministros = suministros; }
    public List<Cliente> getClientes() { return clientes; }
    public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }
}
