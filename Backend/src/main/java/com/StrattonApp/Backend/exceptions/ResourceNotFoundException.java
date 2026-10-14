package com.StrattonApp.Backend.exceptions;

import org.springframework.http.HttpStatus;

/** 404: el recurso no existe. */
public class ResourceNotFoundException extends GlobalException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String mensaje) {
        super(mensaje, HttpStatus.NOT_FOUND);
    }
}
