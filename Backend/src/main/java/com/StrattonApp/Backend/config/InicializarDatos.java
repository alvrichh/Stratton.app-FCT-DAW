package com.StrattonApp.Backend.config;

import com.StrattonApp.Backend.entities.Asesoria;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.repository.AsesoriaRepository;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InicializarDatos implements CommandLineRunner {

    private final EmpleadoRepository empleadoRepository;
    private final AsesoriaRepository asesoriaRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public InicializarDatos(EmpleadoRepository empleadoRepository, AsesoriaRepository asesoriaRepository, ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.asesoriaRepository = asesoriaRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        limpiarTablas();
        inicializarAsesorias();
        inicializarEmpleados();
        inicializarClientes();
    }

    private void limpiarTablas() {
        clienteRepository.deleteAll();
        empleadoRepository.deleteAll();
        asesoriaRepository.deleteAll();
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
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(rolesAdmin);
            empleadoRepository.save(admin);

            Empleado usuario = new Empleado();
            usuario.setFirstName("Usuario");
            usuario.setLastName("Usuario");
            usuario.setUsername("usuario");
            usuario.setEmail("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("usuario"));
            usuario.setRoles(rolesUsuario);
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
                Cliente cliente1 = new Cliente();
                cliente1.setNombre("Cliente1");
                cliente1.setApellidos("Apellido1");
                cliente1.setDNI("12345678A");
                cliente1.setCorreo("cliente1@example.com");
                cliente1.setDireccion("Direccion 1");
                cliente1.setTelefono(123456789);
                cliente1.setIBAN("ES1234567890123456789012");
                cliente1.setEmpleado(admin);
                clienteRepository.save(cliente1);

                Cliente cliente2 = new Cliente();
                cliente2.setNombre("Cliente2");
                cliente2.setApellidos("Apellido2");
                cliente2.setDNI("87654321B");
                cliente2.setCorreo("cliente2@example.com");
                cliente2.setDireccion("Direccion 2");
                cliente2.setTelefono(987654321);
                cliente2.setIBAN("ES2109876543210987654321");
                cliente2.setEmpleado(usuario);
                clienteRepository.save(cliente2);
            }
        }
    }
}
