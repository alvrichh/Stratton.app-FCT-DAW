package com.StrattonApp.Backend.config;

import com.StrattonApp.Backend.entities.*;
import com.StrattonApp.Backend.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class InicializarDatos implements CommandLineRunner {

	private final EmpleadoRepository empleadoRepository;
	private final AsesoriaRepository asesoriaRepository;
	private final ClienteRepository clienteRepository;
	private final PasswordEncoder passwordEncoder;
	private final SuministroRepository suministroRepository;
	private final ComercializadoraRepository comercializadoraRepository;
	
	final Asesoria asesoria1 = new Asesoria();
	final Asesoria asesoria2 = new Asesoria();
	final Comercializadora iberdrola = new Comercializadora();
	final Comercializadora naturgy = new Comercializadora();
	final Comercializadora endesa = new Comercializadora();


	public InicializarDatos(EmpleadoRepository empleadoRepository, AsesoriaRepository asesoriaRepository,
			ClienteRepository clienteRepository, PasswordEncoder passwordEncoder,
			SuministroRepository suministroRepository, ComercializadoraRepository comercializadoraRepository) {
		this.empleadoRepository = empleadoRepository;
		this.asesoriaRepository = asesoriaRepository;
		this.clienteRepository = clienteRepository;
		this.passwordEncoder = passwordEncoder;
		this.suministroRepository = suministroRepository;
		this.comercializadoraRepository = comercializadoraRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		limpiarTablas();
		inicializarAsesorias();
		inicializarEmpleados();
		inicializarComercializadoras();
		inicializarClientes();
		inicializarSuministros();
	}

	private void limpiarTablas() {
		suministroRepository.deleteAll();
		clienteRepository.deleteAll();
		empleadoRepository.deleteAll();
		asesoriaRepository.deleteAll();
		comercializadoraRepository.deleteAll();
	}

	private void inicializarEmpleados() {
		// Verificar si ya existen empleados
		if (empleadoRepository.count() == 0) {
			// Crear un conjunto de roles para el primer empleado (admin)
			Set<Role> rolesAdmin = new HashSet<>();
			rolesAdmin.add(Role.ADMIN);

			// Crear un conjunto de roles para el segundo empleado (usuario)
			Set<Role> rolesUsuario = new HashSet<>();
			rolesUsuario.add(Role.USER);

			// Crear dos empleados de ejemplo
			Empleado admin = new Empleado();
			admin.setNombre("Admin");
			admin.setApellidos("Admin");
			admin.setUsername("admin");
			admin.setEmail("admin@example.com");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setRoles(rolesAdmin);
			admin.setAsesoria(asesoria1);
			empleadoRepository.save(admin);

			Empleado usuario = new Empleado();
			usuario.setNombre("Usuario");
			usuario.setApellidos("Usuario");
			usuario.setUsername("usuario");
			usuario.setEmail("usuario@example.com");
			usuario.setPassword(passwordEncoder.encode("usuario"));
			usuario.setRoles(rolesUsuario);
			admin.setAsesoria(asesoria2);
			empleadoRepository.save(usuario);
		}
	}


	private void inicializarAsesorias() {
		// Verificar si ya existen asesorías
		if (asesoriaRepository.count() == 0) {
			// Crear asesorías de ejemplo
			asesoria1.setNombre("Asesoría 1");
			asesoria1.setDescripcion("Descripción de la asesoría 1");
			asesoria1.setDireccion("Edificio pasarela, Mairena del aljarafe");
			asesoriaRepository.save(asesoria1);

			asesoria2.setNombre("Asesoría 2");
			asesoria2.setDescripcion("Descripción de la asesoría 2");
			asesoria2.setDireccion("Calle Nobel 7, Mairena del aljarafe");
			asesoriaRepository.save(asesoria2);
		}
	}

	private void inicializarClientes() {
		// Verificar si ya existen clientes
		if (clienteRepository.count() == 0) {
			// Obtener los empleados existentes
			Empleado admin = empleadoRepository.findByUsername("admin").orElse(null);
			Empleado usuario = empleadoRepository.findByUsername("usuario").orElse(null);

			if (admin != null && usuario != null) {
				// Crear clientes y asociarlos a los empleados
				for (int i = 0; i < 10; i++) {
					Cliente cliente = new Cliente();
					cliente.setNombre("Cliente" + (i + 1));
					cliente.setApellidos("Apellido" + (i + 1));
					cliente.setDNI(generarDNI());
					cliente.setEmail("cliente" + (i + 1) + "@example.com");
					cliente.setDireccion("Dirección " + (i + 1));
					cliente.setTelefono(generarTelefono());
					cliente.setIBAN(generarIBAN());
					cliente.setEmpleado(i % 2 == 0 ? admin : usuario);
					cliente.setAsesoria(asesoria1);
					cliente.setComercializadora(endesa);
					clienteRepository.save(cliente);
				}
			}
		}
	}

	private void inicializarComercializadoras() {
	    if (comercializadoraRepository.count() == 0) {
	        iberdrola.setNombre("Iberdrola");
	        iberdrola.setPlan("Plan Iberdrola");
	        comercializadoraRepository.save(iberdrola);

	        naturgy.setNombre("Naturgy");
	        naturgy.setPlan("Plan Naturgy");
	        comercializadoraRepository.save(naturgy);

	        endesa.setNombre("Endesa");
	        endesa.setPlan("Plan Endesa");
	        comercializadoraRepository.save(endesa);
	    }
	}

	 private void inicializarSuministros() {
	        // Obtener todos los clientes y comercializadoras existentes
	        Iterable<Cliente> clientes = clienteRepository.findAll();
	        Iterable<Comercializadora> comercializadoras = comercializadoraRepository.findAll();

	        for (Cliente cliente : clientes) {
	            Set<Suministro> suministros = new HashSet<>();

	            // Crear tres suministros para cada cliente
	            for (int i = 0; i < 3; i++) {
	                Suministro suministro = new Suministro();
	                suministro.setCups("CUPS-" + cliente.getIdCliente() + "-" + i);
	                suministro.setEstado(Estado.ACTIVO); // Por ejemplo, todos los suministros inicializados como activos
	                suministro.setDireccion(cliente.getDireccion());
	                suministro.setMensaje("Mensaje de ejemplo para suministro " + i);
	                suministro.setPotencia(10.0); // Potencia de ejemplo

	                // Asignar cliente y comercializadora al suministro
	                suministro.setCliente(cliente);
	                suministro.setComercializadora(comercializadoras.iterator().next()); // Asignar la primera comercializadora por simplicidad

	                suministros.add(suministro);
	            }

	            // Guardar todos los suministros creados para el cliente
	            suministroRepository.saveAll(suministros);

	            // Asignar los suministros al cliente
	            cliente.setSuministros(suministros);
	            clienteRepository.save(cliente);
	        }
	    }
	
	private String generarCUPS() {
		// Generar CUPS aleatorio de ejemplo
		Random random = new Random();
		StringBuilder cups = new StringBuilder();
		String caracteres = "abcdefghijklmnopqrstuvwxyz0123456789";
		for (int i = 0; i < 16; i++) {
			cups.append(caracteres.charAt(random.nextInt(caracteres.length())));
		}
		return cups.toString();
	}

	private String generarDNI() {
		// Generar DNI aleatorio de ejemplo
		Random random = new Random();
		StringBuilder dni = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			dni.append(random.nextInt(10));
		}
		char letra = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(Integer.parseInt(dni.toString()) % 23);
		dni.append(letra);
		return dni.toString();
	}

	private int generarTelefono() {
		// Generar teléfono aleatorio de ejemplo
		Random random = new Random();
		return random.nextInt(1000000000) + 600000000;
	}

	private String generarIBAN() {
		// Generar IBAN aleatorio de ejemplo
		Random random = new Random();
		StringBuilder iban = new StringBuilder("ES");
		for (int i = 0; i < 22; i++) {
			iban.append(random.nextInt(10));
		}
		return iban.toString();
	}
}
