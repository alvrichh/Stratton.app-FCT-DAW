/*
 * Paquete de las entidades de transferencia de datos de la aplicación.
 */
package com.StrattonApp.Backend.DTO;

import java.util.Set;

import com.StrattonApp.Backend.entities.Cliente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa la entidad Cliente para transferencia de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
	  private Long id;
	    private String nombre;
	    private Set<SuministroDTO> suministros;

}