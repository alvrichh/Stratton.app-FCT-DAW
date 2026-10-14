package com.StrattonApp.Backend.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Asesoría energética: agrupa a empleados y clientes.
 */
@Entity
@Table(name = "asesorias")
public class Asesoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsesoria;

    @NotBlank(message = "El nombre de la asesoría no puede estar vacío.")
    @Column(nullable = false, length = 80)
    private String nombre;

    @NotBlank(message = "La dirección de la asesoría no puede estar vacía.")
    @Column(nullable = false, length = 150)
    private String direccion;

    @Min(0)
    @Max(5)
    private int valoracion;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "asesoria")
    @JsonIgnore
    private List<Cliente> clientes = new ArrayList<>();

    @OneToMany(mappedBy = "asesoria")
    @JsonIgnore
    private List<Empleado> empleados = new ArrayList<>();

    public Asesoria() {
    }

    public Asesoria(String nombre, String direccion, String descripcion, int valoracion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.valoracion = valoracion;
    }

    public Long getIdAsesoria() { return idAsesoria; }
    public void setIdAsesoria(Long idAsesoria) { this.idAsesoria = idAsesoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getValoracion() { return valoracion; }
    public void setValoracion(int valoracion) { this.valoracion = valoracion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Cliente> getClientes() { return clientes; }
    public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }
    public List<Empleado> getEmpleados() { return empleados; }
    public void setEmpleados(List<Empleado> empleados) { this.empleados = empleados; }
}
