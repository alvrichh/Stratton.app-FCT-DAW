/*
 * Paquete de control de excepciones.
 */
package com.StrattonApp.Backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GlobalException extends RuntimeException{
	
	public GlobalException (String msg) {
		super(msg);
	}

}
