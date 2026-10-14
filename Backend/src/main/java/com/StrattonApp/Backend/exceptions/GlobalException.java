package com.StrattonApp.Backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción genérica de la aplicación con el código HTTP que debe devolverse.
 */
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    public GlobalException(String msg) {
        this(msg, HttpStatus.BAD_REQUEST);
    }

    public GlobalException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
