package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "suministros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Suministro {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String nombre;
	    private double precio;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "cliente_id")
	    private Cliente cliente;

}
