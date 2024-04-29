package com.StrattonApp.Backend.DTO;

import java.sql.Date;

public class ClienteDTO {


	    private String nombre;
	    private String companiaContratada;
	    private Date fechaSubidaContrato;
	    
	    
		public String getCompaniaContratada() {
			return companiaContratada;
		}
		public void setCompaniaContratada(String companiaContratada) {
			this.companiaContratada = companiaContratada;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public void setFechaSubidaContrato(Object fechaSubidaContrato2) {
			// TODO Auto-generated method stub
			
		}

	}


