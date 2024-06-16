package com.StrattonApp.Backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa la entidad Cliente para
 * transferencia de datos.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuministroDTO {
	private Long id;
	private String nombre;
	private double precio;

}
