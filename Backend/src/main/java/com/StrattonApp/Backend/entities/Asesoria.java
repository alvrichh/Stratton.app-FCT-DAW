package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Asesoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsesoria;

    @NotBlank(message = "La dirección de la asesoría no puede estar vacía.")
    private String direccion;

    private int valoracion;

    private String nombre;
    
    private String descripcion;
    // Relación con Clientes (una asesoría puede tener varios clientes)
    @OneToMany(mappedBy = "asesoria")
    private List<Cliente> clientes;
    
    // Relación con Clientes (una asesoría puede tener varios empleados)
    @OneToMany(mappedBy = "asesoria")
    @JsonIgnore
    private List<Empleado> empleados;



	public Long getIdAsesoria() {
		return idAsesoria;
	}

	public void setIdAsesoria(Long idAsesoria) {
		this.idAsesoria = idAsesoria;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getValoracion() {
		return valoracion;
	}

	public void setValoracion(int valoracion) {
		this.valoracion = valoracion;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}

}
