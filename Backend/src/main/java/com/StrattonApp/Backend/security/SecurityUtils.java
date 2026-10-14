package com.StrattonApp.Backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.exceptions.GlobalException;

import org.springframework.http.HttpStatus;

/**
 * Acceso al empleado autenticado en la petición actual.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /** Empleado autenticado, o excepción 401 si no hay sesión. */
    public static Empleado currentEmpleado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Empleado empleado) {
            return empleado;
        }
        throw new GlobalException("No hay ningún usuario autenticado", HttpStatus.UNAUTHORIZED);
    }
}
