package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "suministros")
public class Suministro {

    @Id
    @Column(name = "cups", length = 20)
    private String cups;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private Estado estado;

    @NotBlank(message = "La dirección del suministro no puede estar vacía.")
    @Column(name = "direccion", length = 100, nullable = false)
    private String direccion;
    
    @Column(name = "mensaje", length = 255)
    private String mensaje;
    
    @Column(name = "potencia")
    private Double potencia;
    
    // Relación con Cliente (uno o varios suministros pertenece a un cliente)
    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Cliente cliente;

    // Relación con Comercializadora (un contrato es ofrecido por una comercializadora)
    @ManyToOne
    @JoinColumn(name = "idComercializadora")
    private Comercializadora comercializadora;

    public Suministro() {
    }

    public Suministro(String cups, Estado estado, String direccion, String mensaje, Double potencia, Cliente cliente, Comercializadora comercializadora) {
        this.cups = cups;
        this.estado = estado;
        this.direccion = direccion;
        this.mensaje = mensaje;
        this.potencia = potencia;
        this.cliente = cliente;
        this.comercializadora = comercializadora;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Double getPotencia() {
        return potencia;
    }

    public void setPotencia(Double potencia) {
        this.potencia = potencia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Comercializadora getComercializadora() {
        return comercializadora;
    }

    public void setComercializadora(Comercializadora comercializadora) {
        this.comercializadora = comercializadora;
    }
}
