package com.StrattonApp.Backend.config;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Rol;
import com.StrattonApp.Backend.entities.Suministro;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.RolRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;
import com.github.javafaker.Faker;

/**
 * Clase que inicializa los datos de la aplicación al arrancar.
 */
@Component
public class InicializarDatos implements CommandLineRunner {

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private SuministroRepository suministroRepository;

	@Autowired
	private RolRepository rolRepository;

	@Override
	public void run(String... args) throws Exception {
		Faker faker = new Faker();

		// Crear roles de ejemplo
		Rol rolAdmin = new Rol(0L, "ADMIN");
		Rol rolUser = new Rol(0L, "USER");
		rolRepository.save(rolAdmin);
		rolRepository.save(rolUser);

		// Crear empleados de ejemplo
		for (int i = 0; i < 5; i++) {
			Empleado empleado = new Empleado();
			empleado.setNombre(faker.name().fullName());
			empleado.setRol(faker.bool().bool() ? rolAdmin : rolUser);

			Set<Cliente> clientes = new HashSet<>();
			for (int j = 0; j < 3; j++) {
				Cliente cliente = new Cliente();
				cliente.setNombre(faker.name().fullName());

				Set<Suministro> suministros = new HashSet<>();
				for (int k = 0; k < 2; k++) {
					Suministro suministro = new Suministro();
					suministro.setNombre(faker.commerce().productName());
					suministro.setPrecio(faker.number().randomDouble(2, 1, 100));
					suministroRepository.save(suministro);
					suministros.add(suministro);
				}
				cliente.setSuministros(suministros);
				clienteRepository.save(cliente);
				clientes.add(cliente);
			}
			empleado.setClientes(clientes);
			empleadoRepository.save(empleado);
		}
	}
}