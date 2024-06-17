package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa una comercializadora en el sistema.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comercializadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComercializadora;

    @NotBlank(message = "El nombre de la comercializadora no puede estar vacío.")
    private String companiaContratada;

    private String plan;

    // Relación con Suministros (una comercializadora puede tener varios suministros)
    @OneToMany(mappedBy = "comercializadora")
    private List<Suministro> suministros;

    /**
     * Obtiene el ID de la comercializadora.
     *
     * @return ID de la comercializadora
     */
    public Long getIdComercializadora() {
        return idComercializadora;
    }

    /**
     * Establece el ID de la comercializadora.
     *
     * @param idComercializadora ID de la comercializadora a establecer
     */
    public void setIdComercializadora(Long idComercializadora) {
        this.idComercializadora = idComercializadora;
    }

    /**
     * Obtiene el nombre de la comercializadora.
     *
     * @return Nombre de la comercializadora
     */
 
    public String getCompaniaContratada() {
  		return companiaContratada;
  	}
    /**
     * Establece el nombre de la comercializadora.
     *
     * @param nombre Nombre de la comercializadora a establecer
     */
    public void setCompaniaContratada(String companiaContratada) {
		this.companiaContratada = companiaContratada;
	}

    /**
     * Obtiene el plan ofrecido por la comercializadora.
     *
     * @return Plan ofrecido por la comercializadora
     */
    public String getPlan() {
        return plan;
    }

  


	/**
     * Establece el plan ofrecido por la comercializadora.
     *
     * @param plan Plan ofrecido por la comercializadora a establecer
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * Obtiene los suministros asociados a la comercializadora.
     *
     * @return Lista de suministros asociados a la comercializadora
     */
    public List<Suministro> getSuministros() {
        return suministros;
    }

    /**
     * Establece los suministros asociados a la comercializadora.
     *
     * @param suministros Lista de suministros a establecer
     */
    public void setSuministros(List<Suministro> suministros) {
        this.suministros = suministros;
    }
}
