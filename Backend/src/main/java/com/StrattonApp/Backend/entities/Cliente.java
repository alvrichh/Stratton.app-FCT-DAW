package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un cliente en el sistema.
 */
@Table(name = "clientes")
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @NotBlank(message = "El nombre del cliente no puede estar vacío.")
    @Column(name = "Nombre", length = 60, nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Column(name = "Apellidos", length = 60, nullable = false)
    private String apellidos;

    @NotBlank(message = "El dni no puede estar en blanco")
    @Column(name = "dni", length = 10, nullable = false)
    private String dni;

    @NotBlank(message = "El email del cliente no puede estar vacío.")
    @Column(name = "email", length = 60, nullable = false)
    private String email;

    @NotBlank(message = "La dirección del cliente no puede estar vacía.")
    @Column(name = "direccion", length = 60, nullable = true)
    private String direccion;

    @NotNull(message = "El telefono del cliente no puede estar vacío.")
    @Column(name = "telefono", length = 20, nullable = false)
    private Integer telefono;

    @NotBlank(message = "El IBAN del cliente no puede estar vacío.")
    @Column(name = "iban", length = 34, nullable = false)
    private String iban;

    // Relación con Asesoría (muchos clientes pueden estar asociados a una asesoría)
    @ManyToOne
    @JoinColumn(name = "idAsesoria")
    private Asesoria asesoria;

    // Relación con suministros (un cliente puede tener varios suministros)
    @OneToMany(mappedBy = "cliente")
    private Set<Suministro> suministros = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idComercializadora")
    private Comercializadora comercializadora;

    @ManyToOne
    @JoinColumn(name = "empleado")
    private Empleado empleado;

    /**
     * Constructor de la clase Cliente.
     */
    public Cliente() {
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
     * @param idCliente ID del cliente a establecer
     */
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
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
     * @param nombre Nombre del cliente a establecer
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
     * @param apellidos Apellidos del cliente a establecer
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el email del cliente.
     *
     * @return Email del cliente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del cliente.
     *
     * @param email Email del cliente a establecer
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la dirección del cliente.
     *
     * @return Dirección del cliente
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del cliente.
     *
     * @param direccion Dirección del cliente a establecer
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene el teléfono del cliente.
     *
     * @return Teléfono del cliente
     */
    public Integer getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del cliente.
     *
     * @param telefono Teléfono del cliente a establecer
     */
    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la asesoría asociada al cliente.
     *
     * @return Asesoría asociada al cliente
     */
    public Asesoria getAsesoria() {
        return asesoria;
    }

    /**
     * Establece la asesoría asociada al cliente.
     *
     * @param asesoria Asesoría a establecer
     */
    public void setAsesoria(Asesoria asesoria) {
        this.asesoria = asesoria;
    }

    /**
     * Obtiene los suministros asociados al cliente.
     *
     * @return Suministros asociados al cliente
     */
    public Set<Suministro> getSuministros() {
        return suministros;
    }

    /**
     * Establece los suministros asociados al cliente.
     *
     * @param suministros Suministros a establecer
     */
    public void setSuministros(Set<Suministro> suministros) {
        this.suministros = suministros;
    }

    /**
     * Obtiene la comercializadora asociada al cliente.
     *
     * @return Comercializadora asociada al cliente
     */
    public Comercializadora getComercializadora() {
        return comercializadora;
    }

    /**
     * Establece la comercializadora asociada al cliente.
     *
     * @param comercializadora Comercializadora a establecer
     */
    public void setComercializadora(Comercializadora comercializadora) {
        this.comercializadora = comercializadora;
    }

    /**
     * Obtiene el empleado asociado al cliente.
     *
     * @return Empleado asociado al cliente
     */
    public Empleado getEmpleado() {
        return empleado;
    }

    /**
     * Establece el empleado asociado al cliente.
     *
     * @param empleado Empleado a establecer
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    /**
     * Obtiene la fecha de subida del contrato del cliente.
     *
     * @return Fecha de subida del contrato (formato dd/MM/yyyy)
     */
    public String getFechaSubidaContrato() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    /**
     * Obtiene el CUPS del cliente.
     *
     * @return CUPS del cliente (a implementar)
     */
    public String getCups() {
        // TODO: Implementar lógica para obtener el CUPS del cliente
        return "CUPS";
    }

    /**
     * Obtiene la compañía contratada por el cliente.
     *
     * @return Compañía contratada por el cliente (a implementar)
     */
    public String getCompaniaContratada() {
        // TODO: Implementar lógica para obtener la compañía contratada por el cliente
        return null;
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
     * @param dni DNI del cliente a establecer
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el IBAN del cliente.
     *
     * @return IBAN del cliente
     */
    public String getIban() {
        return iban;
    }

    /**
     * Establece el IBAN del cliente.
     *
     * @param iban IBAN del cliente a establecer
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

	public Cliente(Long idCliente, @NotBlank(message = "El nombre del cliente no puede estar vacío.") String nombre,
			@NotBlank(message = "El apellido no puede estar en blanco") String apellidos,
			@NotBlank(message = "El dni no puede estar en blanco") String dni,
			@NotBlank(message = "El email del cliente no puede estar vacío.") String email,
			@NotBlank(message = "La dirección del cliente no puede estar vacío.") String direccion,
			@NotNull(message = "El telefono del cliente no puede estar vacío.") Integer telefono,
			@NotBlank(message = "El IBAN del cliente no puede estar vacío.") String iban, Asesoria asesoria,
			Set<Suministro> suministros, Comercializadora comercializadora, Empleado empleado) {
		super();
		this.idCliente = idCliente;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.email = email;
		this.direccion = direccion;
		this.telefono = telefono;
		this.iban = iban;
		this.asesoria = asesoria;
		this.suministros = suministros;
		this.comercializadora = comercializadora;
		this.empleado = empleado;
	}
    
}
