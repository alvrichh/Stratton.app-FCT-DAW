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
     * Constructor para crear una nueva instancia de JwtAuthenticationResponse con el token proporcionado.
     *
     * @param token Token JWT.
     */
    public JwtAuthenticationResponse(String token) {
        this.token = token;
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
         * Construye una instancia de JwtAuthenticationResponse con los atributos proporcionados.
         *
         * @return Instancia de JwtAuthenticationResponse construida.
         */
        public JwtAuthenticationResponse build() {
            return new JwtAuthenticationResponse(token);
        }
    }
}
