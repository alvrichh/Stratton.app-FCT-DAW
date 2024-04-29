package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Entity
public class Suministro {

    @Id
    private String cups;

    private Estado estado;

    private String mensaje;
    
    private Double potencia;

    // Relación con Cliente (uno o varios suministros pertenece a un cliente)
    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Cliente cliente;

    // Relación con Comercializadora (un contrato es ofrecido por una comercializadora)
    @ManyToOne
    @JoinColumn(name = "idComercializadora")
    private Comercializadora comercializadora;


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



	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Double getPotencia() {
		return potencia;
	}

	public void setPotencia(Double potencia) {
		this.potencia = potencia;
	}

    
}
