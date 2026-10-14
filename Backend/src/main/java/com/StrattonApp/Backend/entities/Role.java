package com.StrattonApp.Backend.entities;

/**
 * Roles de usuario de un empleado.
 * <ul>
 *   <li>USER: empleado/asesor comercial. Gestiona sus propios clientes.</li>
 *   <li>MANAGER: jefe de equipo. Puede ver y gestionar los clientes de todos los empleados.</li>
 *   <li>ADMIN: administrador. Además gestiona los empleados.</li>
 * </ul>
 */
public enum Role {
    USER,
    MANAGER,
    ADMIN;

    /** Nombre de la autoridad de Spring Security (con prefijo ROLE_). */
    public String authority() {
        return "ROLE_" + name();
    }
}
