package com.StrattonApp.Backend.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cliente de la asesoría. Está asignado a un empleado (asesor) y puede tener varios suministros.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    /** DNI (8 dígitos + letra) o NIE (X/Y/Z + 7 dígitos + letra). */
    public static final String DNI_REGEX = "^(\\d{8}[A-Za-z]|[XYZxyz]\\d{7}[A-Za-z])$";
    /** Teléfono español de 9 dígitos (fijo o móvil). RN-2. */
    public static final String TELEFONO_REGEX = "^[6789]\\d{8}$";
    /** IBAN español: ES + 22 dígitos (se admiten espacios). RN-7. */
    public static final String IBAN_REGEX = "^ES\\d{2}(\\s?\\d{4}){5}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @NotBlank(message = "El nombre del cliente no puede estar vacío.")
    @Size(max = 60)
    @Column(name = "nombre", length = 60, nullable = false)
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar en blanco.")
    @Size(max = 80)
    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidos;

    @NotBlank(message = "El DNI no puede estar en blanco.")
    @Pattern(regexp = DNI_REGEX, message = "El DNI/NIE no tiene un formato válido.")
    @Column(name = "dni", length = 10, nullable = false, unique = true)
    private String dni;

    @NotBlank(message = "El email del cliente no puede estar vacío.")
    @Email(message = "El formato del email no es válido.")
    @Size(max = 100)
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Size(max = 150)
    @Column(name = "direccion", length = 150)
    private String direccion;

    @NotBlank(message = "El teléfono del cliente no puede estar vacío.")
    @Pattern(regexp = TELEFONO_REGEX, message = "El teléfono debe tener 9 dígitos y empezar por 6, 7, 8 o 9.")
    @Column(name = "telefono", length = 9, nullable = false, unique = true)
    private String telefono;

    @Pattern(regexp = IBAN_REGEX, message = "El IBAN debe tener el formato ES00 0000 0000 0000 0000 0000.")
    @Column(name = "iban", length = 34)
    private String iban;

    /** Fecha en la que se subió/firmó el contrato con el cliente (RF-10). */
    @Column(name = "fecha_subida_contrato")
    private LocalDate fechaSubidaContrato;

    /** Fecha de nacimiento, para comprobar la mayoría de edad del titular (RN-1). */
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @ManyToOne
    @JoinColumn(name = "idAsesoria")
    @JsonIgnore
    private Asesoria asesoria;

    /** Suministros ordenados por fecha de alta: el primero es el principal. */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaAlta ASC, cups ASC")
    @JsonIgnore
    private List<Suministro> suministros = new ArrayList<>();

    /** Comercializadora principal del cliente (la de su primer suministro, RF-2). */
    @ManyToOne
    @JoinColumn(name = "idComercializadora")
    @JsonIgnore
    private Comercializadora comercializadora;

    /** Empleado (asesor) responsable del cliente (RF-9). */
    @ManyToOne
    @JoinColumn(name = "idEmpleado")
    @JsonIgnore
    private Empleado empleado;

    public Cliente() {
    }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public LocalDate getFechaSubidaContrato() { return fechaSubidaContrato; }
    public void setFechaSubidaContrato(LocalDate fechaSubidaContrato) { this.fechaSubidaContrato = fechaSubidaContrato; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Asesoria getAsesoria() { return asesoria; }
    public void setAsesoria(Asesoria asesoria) { this.asesoria = asesoria; }
    public List<Suministro> getSuministros() { return suministros; }
    public void setSuministros(List<Suministro> suministros) { this.suministros = suministros; }
    public Comercializadora getComercializadora() { return comercializadora; }
    public void setComercializadora(Comercializadora comercializadora) { this.comercializadora = comercializadora; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    /** Añade un suministro manteniendo la relación bidireccional. */
    public void addSuministro(Suministro suministro) {
        suministro.setCliente(this);
        this.suministros.add(suministro);
        if (this.comercializadora == null && suministro.getComercializadora() != null) {
            this.comercializadora = suministro.getComercializadora();
        }
    }

    public void removeSuministro(Suministro suministro) {
        this.suministros.remove(suministro);
        suministro.setCliente(null);
    }
}
