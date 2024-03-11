package com.StrattonApp.Backend.entities;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBanco;

    @NotBlank(message = "El nombre del banco no puede estar vacío.")
    private String nombre;

    // Otros atributos relacionados con la información bancaria

    // Relación con Contratos (un banco puede estar asociado a varios contratos)
    @OneToMany(mappedBy = "banco")
    private List<Contrato> contratos;

	public Long getIdBanco() {
		return idBanco;
	}

	public void setIdBanco(Long idBanco) {
		this.idBanco = idBanco;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Contrato> getContratos() {
		return contratos;
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos;
	}

    
}
