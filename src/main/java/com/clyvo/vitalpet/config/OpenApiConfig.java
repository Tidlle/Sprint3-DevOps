package com.clyvo.vitalpet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vitalPetOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clyvo VitalPet API")
                        .version("1.0.0")
                        .description("API REST para gestão de clínicas veterinárias, tutores, pets, consultas, acompanhamentos e alertas inteligentes."));
    }
}
