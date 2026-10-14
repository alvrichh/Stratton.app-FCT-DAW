package com.StrattonApp.Backend.exceptions;

import org.springframework.http.HttpStatus;

/** 409: el recurso ya existe (DNI, teléfono, usuario o email duplicados). */
public class ConflictException extends GlobalException {

    private static final long serialVersionUID = 1L;

    public ConflictException(String msg) {
        super(msg, HttpStatus.CONFLICT);
    }
}
