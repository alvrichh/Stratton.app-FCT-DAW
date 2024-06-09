package com.StrattonApp.Backend.DTO;

import java.io.Serializable;
import java.util.List;

/**
 * DTO (Objeto de Transferencia de Datos) que representa la información de un usuario para la respuesta.
 */
public class EmpleadoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Apellidos del usuario.
     */
    private String apellidos;

    /**
     * Correo electrónico del usuario.
     */
    private String email;

    /**
     * Nombre de usuario del usuario.
     */
    private String username;

    /**
     * Rol del usuario.
     */
    private String rol;

    /**
     * Lista de clientes asociados al empleado.
     */
    private List<ClienteDTO> clientes;

    /**
     * Constructor para crear un nuevo EmpleadoDTO con la información proporcionada.
     *
     * @param nombre     Nombre del usuario.
     * @param apellidos  Apellidos del usuario.
     * @param email      Correo electrónico del usuario.
     * @param username   Nombre de usuario del usuario.
     * @param rol        Rol del usuario.
     * @param clientes   Lista de clientes.
     */
    public EmpleadoDTO(String nombre, String apellidos, String email, String username, String rol, List<ClienteDTO> clientes) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.username = username;
        this.rol = rol;
        this.clientes = clientes;
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param username Nombre de usuario.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre Nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del usuario.
     *
     * @return Apellidos del usuario.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     *
     * @param apellidos Apellidos del usuario.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return Correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email Correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el rol del usuario.
     *
     * @return Rol del usuario.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     *
     * @param rol Rol del usuario.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene la lista de clientes del empleado.
     *
     * @return Lista de clientes.
     */
    public List<ClienteDTO> getClientes() {
        return clientes;
    }

    /**
     * Establece la lista de clientes del empleado.
     *
     * @param clientes Lista de clientes.
     */
    public void setClientes(List<ClienteDTO> clientes) {
        this.clientes = clientes;
    }
}
