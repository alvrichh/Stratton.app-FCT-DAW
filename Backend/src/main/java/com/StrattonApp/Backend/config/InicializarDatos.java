package com.StrattonApp.Backend.config;

import com.StrattonApp.Backend.entities.Asesoria;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.repository.AsesoriaRepository;
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
    private final PasswordEncoder passwordEncoder;

    public InicializarDatos(EmpleadoRepository empleadoRepository, AsesoriaRepository asesoriaRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.asesoriaRepository = asesoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        inicializarEmpleados();
        inicializarAsesorias();
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
}
