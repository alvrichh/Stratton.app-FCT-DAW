package com.StrattonApp.Backend.config;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.StrattonApp.Backend.entities.Asesoria;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Comercializadora;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Estado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.entities.Suministro;
import com.StrattonApp.Backend.repository.AsesoriaRepository;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.ComercializadoraRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;

/**
 * Carga datos de ejemplo al arrancar, SOLO si la base de datos está vacía
 * (o si {@code app.seed.reset=true}, en cuyo caso la vacía primero).
 * <p>
 * Usuarios de ejemplo: {@code admin/admin} (ADMIN), {@code jefe/jefe} (MANAGER) y {@code usuario/usuario} (USER).
 */
@Component
public class InicializarDatos implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializarDatos.class);

    private final EmpleadoRepository empleadoRepository;
    private final AsesoriaRepository asesoriaRepository;
    private final ClienteRepository clienteRepository;
    private final SuministroRepository suministroRepository;
    private final ComercializadoraRepository comercializadoraRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.seed.reset:false}")
    private boolean seedReset;

    /** Permite forzar el reseteo desde los tests. */
    public void setSeedReset(boolean seedReset) {
        this.seedReset = seedReset;
    }

    public InicializarDatos(EmpleadoRepository empleadoRepository, AsesoriaRepository asesoriaRepository,
            ClienteRepository clienteRepository, SuministroRepository suministroRepository,
            ComercializadoraRepository comercializadoraRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.asesoriaRepository = asesoriaRepository;
        this.clienteRepository = clienteRepository;
        this.suministroRepository = suministroRepository;
        this.comercializadoraRepository = comercializadoraRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String LOGOS = "assets/images/comercializadoras/";

    /**
     * Comercializadoras con las que trabaja la asesoría: {nombre, plan de referencia, logo}.
     * Se sincronizan en cada arranque (se añaden las que falten y se actualiza su logo),
     * haya o no datos de ejemplo.
     */
    static final String[][] CATALOGO_COMERCIALIZADORAS = {
        { "Iberdrola", "Plan Estable", LOGOS + "iberdrola.png" },
        { "Endesa", "One Luz", LOGOS + "endesa.png" },
        { "Naturgy", "Tarifa Por Uso Luz", LOGOS + "naturgy.png" },
        { "TotalEnergies", "A Tu Aire Luz", LOGOS + "totalenergies.svg" },
        { "Repsol", "Tarifa Ahorro Plus", LOGOS + "repsol.svg" },
        { "Plenitude", "Fix Luz", LOGOS + "plenitude.svg" },
        { "Niba", "Tarifa Niba", LOGOS + "niba.svg" },
        { "Gana Energía", "Tarifa Fija 24h", LOGOS + "gana-energia.svg" },
        { "Fox Energía", "Tarifa Fox", LOGOS + "fox-energia.png" },
        { "CHC Energía", "Tarifa Fija", LOGOS + "chc-energia.svg" },
        { "Holaluz", "Tarifa Justa", LOGOS + "holaluz.png" },
        { "Octopus Energy", "Octopus Relax", LOGOS + "octopus-energy.svg" },
        { "Audax Renovables", "Tarifa Fija", LOGOS + "audax-renovables.svg" },
        { "Factor Energía", "Tarifa Fija", LOGOS + "factor-energia.svg" },
        { "Som Energia", "Tarifa 2.0TD", LOGOS + "som-energia.svg" },
    };

    /**
     * Entradas antiguas del catálogo que se retiran si ningún cliente ni suministro las usa:
     * "Niva Energía" era un error de nombre (la marca es Niba) y Aldro Energía pasó a ser Plenitude en 2022.
     */
    static final List<String> COMERCIALIZADORAS_RETIRADAS = List.of("Niva Energía", "Aldro Energía");

    @Override
    @Transactional
    public void run(String... args) {
        if (seedEnabled && seedReset) {
            log.warn("app.seed.reset=true: vaciando la base de datos antes de sembrar");
            limpiarTablas();
        }
        sincronizarComercializadoras();
        if (!seedEnabled) {
            log.info("Carga de datos de ejemplo desactivada (app.seed.enabled=false)");
            return;
        }
        if (empleadoRepository.count() > 0) {
            log.info("La base de datos ya contiene datos: no se cargan datos de ejemplo");
            return;
        }
        log.info("Base de datos vacía: cargando datos de ejemplo");

        Asesoria asesoria1 = asesoriaRepository.save(new Asesoria("Stratton Global Sevilla",
                "Edificio Pasarela, Mairena del Aljarafe", "Asesoría energética para particulares y pymes", 5));
        Asesoria asesoria2 = asesoriaRepository.save(new Asesoria("Stratton Global Aljarafe",
                "Calle Nobel 7, Mairena del Aljarafe", "Oficina comercial del Aljarafe", 4));

        List<Comercializadora> comercializadoras = List.of(
                comercializadora("Iberdrola"), comercializadora("Naturgy"), comercializadora("Endesa"));

        Empleado admin = crearEmpleado("Admin", "Stratton", "admin", "admin@strattonapp.com", "admin", Set.of(Role.ADMIN), asesoria1);
        Empleado jefe = crearEmpleado("Marta", "Jefa de Equipo", "jefe", "jefe@strattonapp.com", "jefe", Set.of(Role.MANAGER), asesoria1);
        Empleado usuario = crearEmpleado("Usuario", "Asesor", "usuario", "usuario@strattonapp.com", "usuario", Set.of(Role.USER), asesoria2);
        List<Empleado> empleados = List.of(admin, jefe, usuario);

        String[] nombres = { "Juan", "María", "Pedro", "Lucía", "Carlos", "Ana", "Javier", "Laura", "Miguel", "Elena" };
        String[] apellidos = { "Pérez López", "García Ruiz", "Martínez Sanz", "Rodríguez Gil", "Sánchez Mora",
                "Fernández Vega", "Gómez Castro", "Díaz Ortega", "Romero Navarro", "Torres Prieto" };
        Random random = new Random(42);
        int cupsSeq = 1;

        for (int i = 0; i < nombres.length; i++) {
            Empleado empleado = empleados.get(i % empleados.size());
            Comercializadora comercializadora = comercializadoras.get(i % comercializadoras.size());

            Cliente cliente = new Cliente();
            cliente.setNombre(nombres[i]);
            cliente.setApellidos(apellidos[i]);
            cliente.setDni(generarDNI(10000000 + i * 1234567));
            cliente.setEmail(nombres[i].toLowerCase().replace("í", "i").replace("é", "e") + (i + 1) + "@example.com");
            cliente.setTelefono(String.valueOf(600000000 + i * 1111111));
            cliente.setDireccion("Calle Ejemplo " + (i + 1) + ", Sevilla");
            cliente.setIban(generarIBAN(random));
            cliente.setFechaSubidaContrato(LocalDate.now().minusDays(i * 7L));
            cliente.setFechaNacimiento(LocalDate.of(1970 + i * 3, 1 + i, 10));
            cliente.setEmpleado(empleado);
            cliente.setAsesoria(empleado.getAsesoria());
            cliente.setComercializadora(comercializadora);

            int numSuministros = 1 + (i % 3);
            for (int s = 0; s < numSuministros; s++) {
                Suministro suministro = new Suministro();
                suministro.setCups(generarCUPS(cupsSeq++));
                suministro.setEstado(Estado.values()[(i + s) % Estado.values().length]);
                suministro.setDireccion(s == 0 ? cliente.getDireccion() : "Calle Secundaria " + s + ", Sevilla");
                suministro.setMensaje(s == 0 ? "Contrato principal" : "Segundo punto de suministro");
                suministro.setPotencia(3.45 + s * 1.15);
                suministro.setComercializadora(comercializadoras.get((i + s) % comercializadoras.size()));
                cliente.addSuministro(suministro);
            }
            clienteRepository.save(cliente);
        }
        log.info("Datos de ejemplo cargados: {} empleados, {} clientes, {} suministros",
                empleadoRepository.count(), clienteRepository.count(), suministroRepository.count());
    }

    /**
     * Sincroniza el catálogo: añade las comercializadoras que falten, actualiza su logo y retira
     * las entradas obsoletas que no estén en uso. No toca el plan de las existentes.
     */
    private void sincronizarComercializadoras() {
        int nuevas = 0;
        int logos = 0;
        int retiradas = 0;
        for (String[] c : CATALOGO_COMERCIALIZADORAS) {
            Comercializadora existente = comercializadoraRepository.findByCompaniaContratadaIgnoreCase(c[0]).orElse(null);
            if (existente == null) {
                comercializadoraRepository.save(new Comercializadora(c[0], c[1], c[2]));
                nuevas++;
            } else if (c[2] != null && !c[2].equals(existente.getLogo())) {
                existente.setLogo(c[2]);
                comercializadoraRepository.save(existente);
                logos++;
            }
        }
        for (String nombre : COMERCIALIZADORAS_RETIRADAS) {
            Comercializadora antigua = comercializadoraRepository.findByCompaniaContratadaIgnoreCase(nombre).orElse(null);
            if (antigua != null && !clienteRepository.existsByComercializadora(antigua)
                    && !suministroRepository.existsByComercializadora(antigua)) {
                comercializadoraRepository.delete(antigua);
                retiradas++;
            }
        }
        if (nuevas + logos + retiradas > 0) {
            log.info("Catálogo de comercializadoras: {} añadidas, {} logos actualizados, {} retiradas ({} en total)",
                    nuevas, logos, retiradas, comercializadoraRepository.count());
        }
    }

    private Comercializadora comercializadora(String nombre) {
        return comercializadoraRepository.findByCompaniaContratadaIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalStateException("Falta la comercializadora " + nombre + " en el catálogo"));
    }

    /** Vacía las tablas respetando las claves foráneas (hijos antes que padres, con flush entre pasos). */
    private void limpiarTablas() {
        suministroRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
        // deleteAll() (no en batch) para que Hibernate borre también la tabla usuario_rol
        empleadoRepository.deleteAll();
        empleadoRepository.flush();
        asesoriaRepository.deleteAllInBatch();
        comercializadoraRepository.deleteAllInBatch();
        comercializadoraRepository.flush();
    }

    private Empleado crearEmpleado(String nombre, String apellidos, String username, String email, String password,
            Set<Role> roles, Asesoria asesoria) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellidos(apellidos);
        e.setUsername(username);
        e.setEmail(email);
        e.setPassword(passwordEncoder.encode(password));
        e.setRoles(new java.util.HashSet<>(roles));
        e.setAsesoria(asesoria);
        return empleadoRepository.save(e);
    }

    /** CUPS de ejemplo con formato válido: ES + 16 dígitos + 2 letras de control. */
    static String generarCUPS(int seq) {
        String digitos = String.format("%016d", 31000000000000L + seq);
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        long numero = Long.parseLong(digitos);
        int resto = (int) (numero % 529);
        return "ES" + digitos + letras.charAt(resto / 23) + letras.charAt(resto % 23);
    }

    /** DNI con letra de control correcta. */
    static String generarDNI(int numero) {
        return String.format("%08d", numero) + "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numero % 23);
    }

    /** IBAN español de ejemplo (ES + 22 dígitos). */
    private static String generarIBAN(Random random) {
        StringBuilder iban = new StringBuilder("ES");
        for (int i = 0; i < 22; i++) {
            iban.append(random.nextInt(10));
        }
        return iban.toString();
    }
}
