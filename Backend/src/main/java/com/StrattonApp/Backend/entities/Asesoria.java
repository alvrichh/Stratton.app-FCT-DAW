/*
 * Paquete de las entidades de la aplicación.
 */
package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entidad que representa una asesoría en el sistema.
 */
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
    @JsonIgnore
    private List<Cliente> clientes;
    
    // Relación con Empleados (una asesoría puede tener varios empleados)
    @OneToMany(mappedBy = "asesoria")
    @JsonIgnore
    private List<Empleado> empleados;

    /**
     * Obtiene el ID de la asesoría.
     *
     * @return ID de la asesoría
     */
    public Long getIdAsesoria() {
        return idAsesoria;
    }

    /**
     * Establece el ID de la asesoría.
     *
     * @param idAsesoria ID de la asesoría a establecer
     */
    public void setIdAsesoria(Long idAsesoria) {
        this.idAsesoria = idAsesoria;
    }

    /**
     * Obtiene la dirección de la asesoría.
     *
     * @return Dirección de la asesoría
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la asesoría.
     *
     * @param direccion Dirección de la asesoría a establecer
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene la valoración de la asesoría.
     *
     * @return Valoración de la asesoría
     */
    public int getValoracion() {
        return valoracion;
    }

    /**
     * Establece la valoración de la asesoría.
     *
     * @param valoracion Valoración de la asesoría a establecer
     */
    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    /**
     * Obtiene la lista de clientes asociados a la asesoría.
     *
     * @return Lista de clientes asociados a la asesoría
     */
    public List<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Establece la lista de clientes asociados a la asesoría.
     *
     * @param clientes Lista de clientes a establecer
     */
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    /**
     * Obtiene el nombre de la asesoría.
     *
     * @return Nombre de la asesoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la asesoría.
     *
     * @param nombre Nombre de la asesoría a establecer
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la asesoría.
     *
     * @return Descripción de la asesoría
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la asesoría.
     *
     * @param descripcion Descripción de la asesoría a establecer
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la lista de empleados asociados a la asesoría.
     *
     * @return Lista de empleados asociados a la asesoría
     */
    public List<Empleado> getEmpleados() {
        return empleados;
    }

    /**
     * Establece la lista de empleados asociados a la asesoría.
     *
     * @param empleados Lista de empleados a establecer
     */
    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }
}
