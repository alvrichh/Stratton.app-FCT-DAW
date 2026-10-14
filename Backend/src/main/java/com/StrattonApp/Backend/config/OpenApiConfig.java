package com.StrattonApp.Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Documentación OpenAPI (Swagger UI en /swagger-ui.html) con soporte de token Bearer.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";

    @Bean
    public OpenAPI strattonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StrattonApp API")
                        .description("API REST para la gestión de empleados, clientes y suministros de una asesoría energética.")
                        .version("v2")
                        .contact(new Contact().name("Álvaro Rodríguez Molina").url("https://github.com/alvrichh")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme().name(SCHEME).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
