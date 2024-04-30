package com.StrattonApp.Backend.DTO;

import java.util.Date;

public class ClienteDTO {

    private Long id;
    private String cups;
    private String companiaContratada;
    private Date fechaSubidaContrato;

    public String getCompaniaContratada() {
        return companiaContratada;
    }

    public void setCompaniaContratada(String companiaContratada) {
        this.companiaContratada = companiaContratada;
    }

    /**
     * Establece la fecha de subida del contrato como la fecha actual.
     */
    public void setFechaSubidaContrato() {
        this.fechaSubidaContrato = new Date(); // Asigna la fecha actual
    }

	public String getCups() {
		return cups;
	}

	public void setCups(String cups) {
		this.cups = cups;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
