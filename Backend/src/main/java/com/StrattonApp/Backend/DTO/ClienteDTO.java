package com.StrattonApp.Backend.DTO;

public class ClienteDTO {

    private Long idCliente;
    private String cups;
    private String companiaContratada;
    private String nombre;
    private String apellidos;
    private String dni;
    private String fechaSubidaContrato;
    private String email;
    
    public ClienteDTO() {
    }

 
    // Getters y setters

	public ClienteDTO(Long idCliente, String cups, String companiaContratada, String nombre, String apellidos,
			String dni, String fechaSubidaContrato, String email) {
		this.idCliente = idCliente;
		this.cups = cups;
		this.companiaContratada = companiaContratada;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.fechaSubidaContrato = fechaSubidaContrato;
		this.email = email;
	}


	public Long getIdCliente() {
        return idCliente;
    }

	public void setId(Long idCliente) {
        this.idCliente = idCliente;
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

    public String getFechaSubidaContrato() {
        return fechaSubidaContrato;
    }

    public void setFechaSubidaContrato(String fechaSubidaContrato) {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
