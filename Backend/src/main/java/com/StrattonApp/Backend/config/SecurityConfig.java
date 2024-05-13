package com.StrattonApp.Backend.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.StrattonApp.Backend.service.EmpleadoService;


/**
 * Configuración de seguridad para la aplicación con JWT
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    EmpleadoService servicioempleado;

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * @param http Configuración de seguridad HTTP.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error al configurar la seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request ->
                request
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                    // API CRUD EMPLEADO
                    .requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/admin/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**").hasAuthority("ADMIN")
                    
                    // API CRUD CLIENTE
                    .requestMatchers(HttpMethod.GET, "/strattonapp/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/strattonapp/cliente/**").hasAnyAuthority("MANAGER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/strattonapp/cliente/**").hasAnyAuthority("MANAGER", "ROLE_ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/strattonapp/cliente/**").hasAnyAuthority("MANAGER", "ADMIN")
                    
                    .anyRequest().authenticated())
            .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
            .cors(Customizer.withDefaults())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el encriptador de contraseñas.
     *
     * @return El encriptador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación DAO.
     *
     * @return El proveedor de autenticación DAO configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(servicioempleado.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura el administrador de autenticación.
     *
     * @param config Configuración de autenticación.
     * @return El administrador de autenticación configurado.
     * @throws Exception Si ocurre un error al obtener el administrador de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el origen de configuración CORS.
     *
     * @return El origen de configuración CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080")); // Permitir solicitudes desde localhost en el puerto 8080
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Permitir estos métodos HTTP
        configuration.setAllowCredentials(true); // Permitir credenciales (por ejemplo, cookies)
        configuration.addAllowedHeader("*"); // Permitir todos los encabezados
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;*/
}
