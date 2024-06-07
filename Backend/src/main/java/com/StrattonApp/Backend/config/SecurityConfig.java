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
import com.StrattonApp.Backend.config.AuthEntryPoint;

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

	@Autowired
	AuthEntryPoint authEntryPoint;
	
	@Autowired
	RequestFilter requestFilter;
	
    @Autowired
    EmpleadoService userService;

	/**
	 * Configura la cadena de filtros de seguridad.
	 *
	 * @param http Configuración de seguridad HTTP.
	 * @return La cadena de filtros de seguridad configurada.
	 * @throws Exception Si ocurre un error al configurar la seguridad.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling((exceptionHandling) -> exceptionHandling.authenticationEntryPoint(authEntryPoint))
				.authorizeHttpRequests(request -> {
					request.requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
							// API CRUD EMPLEADO
							.requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasAuthority("ADMIN")
							.requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasAuthority("ADMIN")
							.requestMatchers(HttpMethod.PUT, "/api/v1/admin/**").hasAuthority("ADMIN")
							.requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**").hasAuthority("ADMIN")

							// API CRUD CLIENTE
							.requestMatchers(HttpMethod.GET, "/strattonapp/**").permitAll()
							.requestMatchers(HttpMethod.POST, "/strattonapp/cliente/**")
							.hasAnyAuthority("MANAGER", "ADMIN")
							.requestMatchers(HttpMethod.PUT, "/strattonapp/cliente/**")
							.hasAnyAuthority("MANAGER", "ROLE_ADMIN")
							.requestMatchers(HttpMethod.DELETE, "/strattonapp/cliente/**")
							.hasAnyAuthority("MANAGER", "ADMIN")

							.anyRequest().permitAll();
				}).formLogin((form) -> form.permitAll()).logout((logout) -> logout.permitAll().logoutSuccessUrl("/"));

		http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	   @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    @Bean
	    public AuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(userService.userDetailsService());
	        authProvider.setPasswordEncoder(passwordEncoder());
	        return authProvider;
	    }

	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	        return config.getAuthenticationManager();
	    }

}