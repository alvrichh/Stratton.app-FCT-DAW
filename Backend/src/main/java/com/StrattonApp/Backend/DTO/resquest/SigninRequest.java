package com.StrattonApp.Backend.DTO.resquest;

import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Clase que representa la solicitud de inicio de sesión de un usuario.
 */
@CrossOrigin // Permite el acceso CORS de cualquier origen a todos los endpoints en este controlador
public class SigninRequest {

    /**
     * Nombre de usuario.
     */
    private String username;

    /**
     * Contraseña del usuario.
     */
    private String password;

    /**
     * Constructor predeterminado de la clase `SigninRequest`.
     */
    public SigninRequest() {
    }

    /**
     * Constructor con parámetros de la clase `SigninRequest`.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña del usuario.
     */
    public SigninRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
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