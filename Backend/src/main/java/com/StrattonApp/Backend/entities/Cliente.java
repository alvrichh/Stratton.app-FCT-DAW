package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

@Table(name = "clientes")
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @NotBlank(message = "El nombre del cliente no puede estar vacío.")
    @Column(name= "Nombre", length = 60, nullable= false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Column(name= "Apellidos", length = 60, nullable= false)
    private String apellidos;

    @NotBlank(message = "El dni no puede estar en blanco")
    @Column(name= "dni", length = 10, nullable= false)
    private String DNI;

    @NotBlank(message = "El email del cliente no puede estar vacío.")
    @Column(name= "email", length = 60, nullable= false)
    private String email;

    @NotBlank(message = "La dirección del cliente no puede estar vacío.")
    @Column(name= "direccion", length = 60, nullable= false)
    private String direccion;

    @NotNull(message = "El telefono del cliente no puede estar vacío.")
    @Column(name= "telefono", length = 20, nullable= false)
    private Integer telefono;
    
    @NotBlank(message = "El IBAN del cliente no puede estar vacío.")
    @Column(name= "iban", length = 34, nullable= false)
    private String IBAN;
    
    // Relación con Asesoría (muchos clientes pueden estar asociados a una asesoría)
    @ManyToOne
    @JoinColumn(name = "idAsesoria")
    private Asesoria asesoria;

    // Relación con Contrato (un cliente puede tener varios contratos)
    @OneToMany(mappedBy = "cliente")
    private List<Suministro> suministros;

    @ManyToOne
    @JoinColumn(name = "idComercializadora")
    private Comercializadora comercializadora;

    
    @ManyToOne
    @JoinColumn(name = "empleado")
    private Empleado empleado;

	public Cliente(Long idCliente, @NotBlank(message = "El nombre del cliente no puede estar vacío.") String nombre,
			@NotBlank(message = "El apellido no puede estar en blanco") String apellidos,
			@NotBlank(message = "El dni no puede estar en blanco") String dNI,
			@NotBlank(message = "El email del cliente no puede estar vacío.") String email,
			@NotBlank(message = "La dirección del cliente no puede estar vacío.") String direccion,
			@NotNull(message = "El telefono del cliente no puede estar vacío.") Integer telefono,
			@NotBlank(message = "El IBAN del cliente no puede estar vacío.") String iBAN, Asesoria asesoria,
			List<Suministro> suministros, Comercializadora comercializadora, Empleado empleado) {
		super();
		this.idCliente = idCliente;
		this.nombre = nombre;
		this.apellidos = apellidos;
		DNI = dNI;
		this.email = email;
		this.direccion = direccion;
		this.telefono = telefono;
		IBAN = iBAN;
		this.asesoria = asesoria;
		this.suministros = suministros;
		this.comercializadora = comercializadora;
		this.empleado = empleado;
	}

	public Cliente() {
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}


	public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
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

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

	public Asesoria getAsesoria() {
		return asesoria;
	}

	public void setAsesoria(Asesoria asesoria) {
		this.asesoria = asesoria;
	}

	public List<Suministro> getSuministros() {
		return suministros;
	}

	public void setSuministros(List<Suministro> suministros) {
		this.suministros = suministros;
	}
    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

	public Comercializadora getComercializadora() {
		return comercializadora;
	}

	public void setComercializadora(Comercializadora comercializadora) {
		this.comercializadora = comercializadora;
	}


}
