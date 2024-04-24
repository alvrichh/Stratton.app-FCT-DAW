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

/**
 * Configuración de seguridad para la aplicación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
  //  UsuarioService userService;

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
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasAuthority("ROLE_ADMIN")
                    
                    // API CRUD CLIENTE
                    .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                    
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
       // authProvider.setUserDetailsService(userService.userDetailsService());
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
}
