package com.StrattonApp.Backend.DTO.response;

/**
 * Respuesta de autenticación JWT que contiene el token JWT generado.
 */
public class JwtAuthenticationResponse {

    /**
     * Token JWT.
     */
    private String token;

    /**
     * Rol del usuario autenticado.
     */
    private String role;

    /**
     * Constructor para crear una nueva instancia de JwtAuthenticationResponse con el token y el rol proporcionados.
     *
     * @param token Token JWT.
     * @param role  Rol del usuario autenticado.
     */
    public JwtAuthenticationResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    /**
     * Obtiene el token JWT.
     *
     * @return Token JWT.
     */
    public String getToken() {
        return token;
    }

    /**
     * Establece el token JWT.
     *
     * @param token Token JWT.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obtiene el rol del usuario autenticado.
     *
     * @return Rol del usuario autenticado.
     */
    public String getRole() {
        return role;
    }

    /**
     * Establece el rol del usuario autenticado.
     *
     * @param role Rol del usuario autenticado.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Crea un nuevo constructor de JwtAuthenticationResponse.
     *
     * @return Constructor de JwtAuthenticationResponse.
     */
    public static JwtAuthenticationResponseBuilder builder() {
        return new JwtAuthenticationResponseBuilder();
    }

    /**
     * Constructor interno para facilitar la construcción de instancias de JwtAuthenticationResponse.
     */
    public static class JwtAuthenticationResponseBuilder {

        /**
         * Token JWT.
         */
        private String token;

        /**
         * Rol del usuario autenticado.
         */
        private String role;

        /**
         * Establece el token JWT.
         *
         * @param token Token JWT.
         * @return Constructor actualizado.
         */
        public JwtAuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * Establece el rol del usuario autenticado.
         *
         * @param role Rol del usuario autenticado.
         * @return Constructor actualizado.
         */
        public JwtAuthenticationResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        /**
         * Construye una instancia de JwtAuthenticationResponse con los atributos proporcionados.
         *
         * @return Instancia de JwtAuthenticationResponse construida.
         */
        public JwtAuthenticationResponse build() {
            return new JwtAuthenticationResponse(token, role);
        }
        
    }
}
