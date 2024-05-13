package com.StrattonApp.Backend.DTO.resquest;

/**
 * Clase que representa la solicitud de registro de un nuevo usuario.
 */
public class SignUpRequest {

    /**
     * Nombre de usuario.
     */
    private String username;

    /**
     * Dirección de correo electrónico.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String password;

    /**
     * Obtiene la dirección de correo electrónico.
     *
     * @return Dirección de correo electrónico.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece la dirección de correo electrónico.
     *
     * @param email Dirección de correo electrónico.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password Contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
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
}