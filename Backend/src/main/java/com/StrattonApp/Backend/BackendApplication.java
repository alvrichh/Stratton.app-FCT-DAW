/*
 * Paquete del backend de la aplicación.
 */
package com.StrattonApp.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class BackendApplication {
	/*
	 * Proyecto TFG desarrollo de aplicaciónes web IES ALIXAR 
	 * @author Álvaro Rodríguez Molina
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
