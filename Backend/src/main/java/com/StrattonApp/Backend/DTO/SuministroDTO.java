package com.StrattonApp.Backend.DTO;

import com.StrattonApp.Backend.entities.Comercializadora;
import com.StrattonApp.Backend.entities.Estado;

/**
 * DTO (Data Transfer Object) que representa la entidad Cliente para transferencia de datos.
 */
public class SuministroDTO {


	private  String cups;
	private String direccion;
	private String nombre;
	private Estado estado;
	private Comercializadora comercializadora;
	public String getCups() {
		return cups;
	}
	public void setCups(String cups) {
		this.cups = cups;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Comercializadora getComercializadora() {
		return comercializadora;
	}
	public void setComercializadora(Comercializadora comercializadora) {
		this.comercializadora = comercializadora;
	}
	public SuministroDTO(String cups, String direccion, String nombre, Estado estado,
			Comercializadora comercializadora) {
		this.cups = cups;
		this.direccion = direccion;
		this.nombre = nombre;
		this.estado = estado;
		this.comercializadora = comercializadora;
	}

	public SuministroDTO() {
		
	}

}
