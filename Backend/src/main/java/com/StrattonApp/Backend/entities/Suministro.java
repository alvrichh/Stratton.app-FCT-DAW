package com.StrattonApp.Backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Punto de suministro (luz o gas) de un cliente, identificado por su CUPS.
 * Un cliente puede tener varios suministros (RF-5 / RF-8).
 */
@Entity
@Table(name = "suministros")
public class Suministro {

    /** Expresión regular del CUPS español: ES + 16 dígitos + 2 letras de control (+ 2 opcionales). */
    public static final String CUPS_REGEX = "^ES\\d{16}[A-Z]{2}(\\d[FPRCXYZ])?$";
    /** Igual que {@link #CUPS_REGEX} pero admite minúsculas: las peticiones se normalizan a mayúsculas en los servicios. */
    public static final String CUPS_REGEX_INPUT = "^(?i)ES\\d{16}[A-Z]{2}(\\d[FPRCXYZ])?$";

    @Id
    @NotBlank(message = "El CUPS no puede estar vacío.")
    @Pattern(regexp = CUPS_REGEX, message = "El CUPS no tiene un formato válido (ej. ES0031408000000000AA0F).")
    @Column(name = "cups", length = 22)
    private String cups;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private Estado estado = Estado.PENDIENTE;

    @NotBlank(message = "La dirección del suministro no puede estar vacía.")
    @Column(name = "direccion", length = 150, nullable = false)
    private String direccion;

    @Column(name = "mensaje", length = 255)
    private String mensaje;

    @Column(name = "potencia")
    private Double potencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCliente", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "idComercializadora")
    private Comercializadora comercializadora;

    public Suministro() {
    }

    public Suministro(String cups, Estado estado, String direccion, String mensaje, Double potencia,
            Cliente cliente, Comercializadora comercializadora) {
        this.cups = cups;
        this.estado = estado;
        this.direccion = direccion;
        this.mensaje = mensaje;
        this.potencia = potencia;
        this.cliente = cliente;
        this.comercializadora = comercializadora;
    }

    public String getCups() { return cups; }
    public void setCups(String cups) { this.cups = cups; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Double getPotencia() { return potencia; }
    public void setPotencia(Double potencia) { this.potencia = potencia; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Comercializadora getComercializadora() { return comercializadora; }
    public void setComercializadora(Comercializadora comercializadora) { this.comercializadora = comercializadora; }
}
