package com.StrattonApp.Backend.DTO;

import java.util.Date;
import java.util.List;

import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Suministro;

public class ClienteDTO {

    private Long id;
    private String cups;
    private String companiaContratada;
    private Date fechaSubidaContrato;
    private String nombre;
    private String apellidos;
    private String dni;

    public ClienteDTO(Long id, String cups, String companiaContratada, Date fechaSubidaContrato, String nombre, String apellidos, String dni) {
        this.id = id;
        this.cups = cups;
        this.companiaContratada = companiaContratada;
        this.fechaSubidaContrato = fechaSubidaContrato;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
    }

    public ClienteDTO(Long idCliente, String cups2, String companiaContratada2, Date fechaSubidaContrato2,
            String nombre2, String apellidos2, String dni2, String email, String direccion, Integer telefono,
            String iban, Long long1, Long long2, Object object) {
        //TODO Auto-generated constructor stub
    }

    public ClienteDTO(Long idCliente, List<Suministro> suministros, String companiaContratada2,
			Date fechaSubidaContrato2, String nombre2, String apellidos2, String dni2, String email, String direccion,
			Integer telefono, String iban, Long long1, Long long2, Long long3) {
		// TODO Auto-generated constructor stub
	}

	public ClienteDTO(Long idCliente, String dni2, String companiaContratada2, Date fechaSubidaContrato2,
			String nombre2, String apellidos2, Date fechaSubidaContrato3) {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public String getCompaniaContratada() {
        return companiaContratada;
    }

    public void setCompaniaContratada(String companiaContratada) {
        this.companiaContratada = companiaContratada;
    }

    public Date getFechaSubidaContrato() {
        return fechaSubidaContrato;
    }

    public void setFechaSubidaContrato(Date fechaSubidaContrato) {
        this.fechaSubidaContrato = fechaSubidaContrato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
