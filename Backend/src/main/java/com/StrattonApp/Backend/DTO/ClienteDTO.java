/*
 * Paquete de las entidades de transferencia de datos de la aplicación.
 */
package com.StrattonApp.Backend.DTO;

/**
 * DTO (Data Transfer Object) que representa la entidad Cliente para transferencia de datos.
 */
public class ClienteDTO {

    private Long idCliente;
    private String cups;
    private String companiaContratada;
    private String fechaSubidaContrato;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private Integer telefono;

    /**
     * Constructor vacío de ClienteDTO.
     */
    public ClienteDTO() {
    }

    /**
     * Constructor de ClienteDTO con todos los atributos.
     *
     * @param idCliente           ID del cliente
     * @param cups                CUPS (Código Universal del Punto de Suministro)
     * @param companiaContratada  Compañía contratada por el cliente
     * @param fechaSubidaContrato Fecha de subida del contrato
     * @param nombre              Nombre del cliente
     * @param apellidos           Apellidos del cliente
     * @param dni                 DNI (Documento Nacional de Identidad) del cliente
     * @param email               Correo electrónico del cliente
     */
    public ClienteDTO(Long idCliente, String cups, String companiaContratada, String fechaSubidaContrato, String nombre,
                      String apellidos, String dni, String email) {
        this.idCliente = idCliente;
        this.cups = cups;
        this.companiaContratada = companiaContratada;
        this.fechaSubidaContrato = fechaSubidaContrato;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
    }

    /**
     * Obtiene el ID del cliente.
     *
     * @return ID del cliente
     */
    public Long getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el ID del cliente.
     *
     * @param idCliente ID del cliente
     */
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el CUPS del cliente.
     *
     * @return CUPS del cliente
     */
    public String getCups() {
        return cups;
    }

    /**
     * Establece el CUPS del cliente.
     *
     * @param cups CUPS del cliente
     */
    public void setCups(String cups) {
        this.cups = cups;
    }

    /**
     * Obtiene la compañía contratada por el cliente.
     *
     * @return Compañía contratada por el cliente
     */
    public String getCompaniaContratada() {
        return companiaContratada;
    }

    /**
     * Establece la compañía contratada por el cliente.
     *
     * @param companiaContratada Compañía contratada por el cliente
     */
    public void setCompaniaContratada(String companiaContratada) {
        this.companiaContratada = companiaContratada;
    }

    /**
     * Obtiene la fecha de subida del contrato del cliente.
     *
     * @return Fecha de subida del contrato del cliente
     */
    public String getFechaSubidaContrato() {
        return fechaSubidaContrato;
    }

    /**
     * Establece la fecha de subida del contrato del cliente.
     *
     * @param fechaSubidaContrato Fecha de subida del contrato del cliente
     */
    public void setFechaSubidaContrato(String fechaSubidaContrato) {
        this.fechaSubidaContrato = fechaSubidaContrato;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return Nombre del cliente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     *
     * @param nombre Nombre del cliente
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del cliente.
     *
     * @return Apellidos del cliente
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del cliente.
     *
     * @param apellidos Apellidos del cliente
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el DNI del cliente.
     *
     * @return DNI del cliente
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del cliente.
     *
     * @param dni DNI del cliente
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el correo electrónico del cliente.
     *
     * @return Correo electrónico del cliente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del cliente.
     *
     * @param email Correo electrónico del cliente
     */
    public void setEmail(String email) {
        this.email = email;
    }

	public Integer getTelefono() {
		return telefono;
	}

	public void setTelefono(Integer telefono) {
		this.telefono = telefono;
	}
}
