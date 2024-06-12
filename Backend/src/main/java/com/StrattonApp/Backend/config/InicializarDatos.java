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
        inicializarClientes();
        inicializarComercializadoras();
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
            admin.setAsesoria(asesoriaRepository.findById(1L).orElse(null));
            empleadoRepository.save(admin);

            Empleado usuario = new Empleado();
            usuario.setNombre("Usuario");
            usuario.setApellidos("Usuario");
            usuario.setUsername("usuario");
            usuario.setEmail("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("usuario"));
            usuario.setRoles(rolesUsuario);
            usuario.setAsesoria(asesoriaRepository.findById(2L).orElse(null));
            empleadoRepository.save(usuario);
        }
    }

    private void inicializarAsesorias() {
        // Verificar si ya existen asesorías
        if (asesoriaRepository.count() == 0) {
            // Crear asesorías de ejemplo
            Asesoria asesoria1 = new Asesoria();
            asesoria1.setNombre("Asesoría 1");
            asesoria1.setDescripcion("Descripción de la asesoría 1");
            asesoriaRepository.save(asesoria1);

            Asesoria asesoria2 = new Asesoria();
            asesoria2.setNombre("Asesoría 2");
            asesoria2.setDescripcion("Descripción de la asesoría 2");
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
                    clienteRepository.save(cliente);
                }
            }
        }
    }

    private void inicializarComercializadoras() {
        // Crear comercializadoras aleatorias de ejemplo
        for (int i = 0; i < 3; i++) {
            Comercializadora comercializadora = new Comercializadora();
            comercializadora.setNombre("Comercializadora " + i);
            comercializadora.setPlan("Plan " + i);
            comercializadoraRepository.save(comercializadora);
        }
    }

    private void inicializarSuministros() {
        // Crear suministros aleatorios de ejemplo
        Random random = new Random();
        Iterable<Comercializadora> comercializadoras = comercializadoraRepository.findAll();
        for (int i = 0; i < 10; i++) {
            Suministro suministro = new Suministro();
            suministro.setCups(generarCUPS());
            suministro.setPotencia(random.nextDouble() * 10); // Potencia aleatoria entre 0 y 10
            suministro.setEstado(Estado.values()[random.nextInt(Estado.values().length)]);
            suministro.setMensaje("Mensaje de estado " + i);
            suministro.setComercializadora(((List<Comercializadora>) comercializadoras).get(random.nextInt((int) comercializadoraRepository.count())));
            suministroRepository.save(suministro);
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
