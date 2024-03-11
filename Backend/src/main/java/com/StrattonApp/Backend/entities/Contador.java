package com.StrattonApp.Backend.entities;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Contador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContador;

    @NotBlank(message = "El CUPS de luz no puede estar vacío.")
    private String CUPS_luz;

    private double consumo_anual;

    private double potencia_contratada;

    // Relación con Contratos (un contador puede estar asociado a varios contratos)
    @OneToMany(mappedBy = "contador")
    private List<Contrato> contratos;

	public Long getIdContador() {
		return idContador;
	}

	public void setIdContador(Long idContador) {
		this.idContador = idContador;
	}

	public String getCUPS_luz() {
		return CUPS_luz;
	}

	public void setCUPS_luz(String cUPS_luz) {
		CUPS_luz = cUPS_luz;
	}

	public double getConsumo_anual() {
		return consumo_anual;
	}

	public void setConsumo_anual(double consumo_anual) {
		this.consumo_anual = consumo_anual;
	}

	public double getPotencia_contratada() {
		return potencia_contratada;
	}

	public void setPotencia_contratada(double potencia_contratada) {
		this.potencia_contratada = potencia_contratada;
	}

	public List<Contrato> getContratos() {
		return contratos;
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos;
	}


}
