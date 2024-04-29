package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Comercializadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComercializadora;

    @NotBlank(message = "El nombre de la comercializadora no puede estar vacío.")
    private String nombre;

    private String plan;

    // Relación con Contratos (una comercializadora puede ofrecer varios contratos)
    @OneToMany(mappedBy = "comercializadora")
    private List<Suministro> suministros;

	public Long getIdComercializadora() {
		return idComercializadora;
	}

	public void setIdComercializadora(Long idComercializadora) {
		this.idComercializadora = idComercializadora;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public List<Suministro> getContratos() {
		return suministros;
	}

	public void setContratos(List<Suministro> contratos) {
		this.suministros = contratos;
	}

}
