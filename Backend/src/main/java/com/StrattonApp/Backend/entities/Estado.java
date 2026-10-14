package com.StrattonApp.Backend.entities;

/**
 * Estado de un suministro (punto de suministro / contrato) en la comercializadora.
 */
public enum Estado {
    /** Contrato activo en la comercializadora. */
    ACTIVO,
    /** Contrato dado de baja o rechazado (K.O.). */
    INACTIVO,
    /** Contrato enviado y pendiente de activación. */
    PENDIENTE
}
