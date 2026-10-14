package com.StrattonApp.Backend.exceptions;

import org.springframework.http.HttpStatus;

/** 403: el usuario autenticado no tiene permiso sobre el recurso. */
public class ForbiddenException extends GlobalException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException(String msg) {
        super(msg, HttpStatus.FORBIDDEN);
    }
}
