package com.tpgl.tpgl.web.controller;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI / Swagger UI (Séance 7 & Tableau Architecture Section 4).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoLocOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoLoc API — Plateforme de gestion de location de véhicules multi-agences")
                        .description("API REST Spring Boot pour l'étude de cas AutoLoc (UP ASI — Architecture des Systèmes d'Information)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AutoLoc Engineering")
                                .email("contact@autoloc.tn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
