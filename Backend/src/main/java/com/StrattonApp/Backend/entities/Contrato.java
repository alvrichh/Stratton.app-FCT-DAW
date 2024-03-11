package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Entity
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrato;

    @NotBlank(message = "El estado del contrato no puede estar vacío.")
    private String estado;

    private Date fecha_inicio;

    private Date fecha_fin;

    // Relación con Cliente (un contrato pertenece a un cliente)
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Relación con Comercializadora (un contrato es ofrecido por una comercializadora)
    @ManyToOne
    @JoinColumn(name = "id_comercializadora")
    private Comercializadora comercializadora;

    // Relación con Contador (un contrato está asociado a un contador)
    @ManyToOne
    @JoinColumn(name = "id_contador")
    private Contador contador;

	public Long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(Long idContrato) {
		this.idContrato = idContrato;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(Date fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public Date getFecha_fin() {
		return fecha_fin;
	}

	public void setFecha_fin(Date fecha_fin) {
		this.fecha_fin = fecha_fin;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Comercializadora getComercializadora() {
		return comercializadora;
	}

	public void setComercializadora(Comercializadora comercializadora) {
		this.comercializadora = comercializadora;
	}

	public Contador getContador() {
		return contador;
	}

	public void setContador(Contador contador) {
		this.contador = contador;
	}

    
}
