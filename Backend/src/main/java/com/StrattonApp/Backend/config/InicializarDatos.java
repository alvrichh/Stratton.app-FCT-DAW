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

/**
 * Clase que inicializa los datos de la aplicación al arrancar.
 */
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

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param empleadoRepository         el repositorio de empleados
     * @param asesoriaRepository         el repositorio de asesorías
     * @param clienteRepository          el repositorio de clientes
     * @param passwordEncoder            el codificador de contraseñas
     * @param suministroRepository       el repositorio de suministros
     * @param comercializadoraRepository el repositorio de comercializadoras
     */
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

    /**
     * Método que se ejecuta al arrancar la aplicación y llama a los métodos para
     * inicializar los datos.
     * 
     * @param args argumentos de la línea de comandos
     * @throws Exception si ocurre un error durante la inicialización
     */
    @Override
    public void run(String... args) throws Exception {
        limpiarTablas();
        inicializarAsesorias();
        inicializarEmpleados();
        inicializarComercializadoras();
        inicializarClientes();
        inicializarSuministros();
    }

    /**
     * Limpia las tablas de la base de datos.
     */
    private void limpiarTablas() {
        suministroRepository.deleteAll();
        clienteRepository.deleteAll();
        empleadoRepository.deleteAll();
        asesoriaRepository.deleteAll();
        comercializadoraRepository.deleteAll();
    }

    /**
     * Inicializa los empleados en la base de datos.
     */
    private void inicializarEmpleados() {
        if (empleadoRepository.count() == 0) {
            Set<Role> rolesAdmin = new HashSet<>();
            rolesAdmin.add(Role.ADMIN);

            Set<Role> rolesUsuario = new HashSet<>();
            rolesUsuario.add(Role.USER);

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
            usuario.setAsesoria(asesoria2);
            empleadoRepository.save(usuario);
        }
    }

    /**
     * Inicializa las asesorías en la base de datos.
     */
    private void inicializarAsesorias() {
        if (asesoriaRepository.count() == 0) {
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

    /**
     * Inicializa los clientes en la base de datos.
     */
    private void inicializarClientes() {
        if (clienteRepository.count() == 0) {
            Empleado admin = empleadoRepository.findByUsername("admin").orElse(null);
            Empleado usuario = empleadoRepository.findByUsername("usuario").orElse(null);

            if (admin != null && usuario != null) {
                for (int i = 0; i < 10; i++) {
                    Cliente cliente = new Cliente();
                    cliente.setNombre("Cliente" + (i + 1));
                    cliente.setApellidos("Apellido" + (i + 1));
                    cliente.setDni(generarDNI());
                    cliente.setEmail("cliente" + (i + 1) + "@example.com");
                    cliente.setDireccion("Dirección " + (i + 1));
                    cliente.setTelefono(generarTelefono());
                    cliente.setIban(generarIBAN());
                    cliente.setEmpleado(i % 2 == 0 ? admin : usuario);
                    cliente.setAsesoria(asesoria1);
                    cliente.setComercializadora(endesa);
                    clienteRepository.save(cliente);
                }
            }
        }
    }

    /**
     * Inicializa las comercializadoras en la base de datos.
     */
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

    /**
     * Inicializa los suministros en la base de datos.
     */
    private void inicializarSuministros() {
        Iterable<Cliente> clientes = clienteRepository.findAll();
        Iterable<Comercializadora> comercializadoras = comercializadoraRepository.findAll();

        for (Cliente cliente : clientes) {
            Set<Suministro> suministros = new HashSet<>();

            for (int i = 0; i < 3; i++) {
                Suministro suministro = new Suministro();
                suministro.setCups("CUPS-" + cliente.getIdCliente() + "-" + i);
                suministro.setEstado(Estado.ACTIVO);
                suministro.setDireccion(cliente.getDireccion());
                suministro.setMensaje("Mensaje de ejemplo para suministro " + i);
                suministro.setPotencia(10.0);

                suministro.setCliente(cliente);
                suministro.setComercializadora(comercializadoras.iterator().next());

                suministros.add(suministro);
            }

            suministroRepository.saveAll(suministros);

            cliente.setSuministros(suministros);
            clienteRepository.save(cliente);
        }
    }

    /**
     * Genera un CUPS aleatorio de ejemplo.
     * No se utiliza pero se mantiene en el código para futuras actualizaciones
     * @return un CUPS aleatorio
     */
    private String generarCUPS() {
        Random random = new Random();
        StringBuilder cups = new StringBuilder();
        String caracteres = "ES00311000000000000000";
        for (int i = 0; i < 16; i++) {
            cups.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return cups.toString();
    }

    /**
     * Genera un DNI aleatorio de ejemplo.
     * 
     * @return un DNI aleatorio
     */
    private String generarDNI() {
        Random random = new Random();
        StringBuilder dni = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            dni.append(random.nextInt(10));
        }
        char letra = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(Integer.parseInt(dni.toString()) % 23);
        dni.append(letra);
        return dni.toString();
    }

    /**
     * Genera un teléfono aleatorio de ejemplo.
     * 
     * @return un número de teléfono aleatorio
     */
    private int generarTelefono() {
        Random random = new Random();
        return random.nextInt(1000000000) + 600000000;
    }

    /**
     * Genera un IBAN aleatorio de ejemplo.
     * 
     * @return un IBAN aleatorio
     */
    private String generarIBAN() {
        Random random = new Random();
        StringBuilder iban = new StringBuilder("ES");
        for (int i = 0; i < 22; i++) {
            iban.append(random.nextInt(10));
        }
        return iban.toString();
    }
}
