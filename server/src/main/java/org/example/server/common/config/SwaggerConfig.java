package org.example.server.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // TODO: JWT 인증 붙이면 아래 주석 해제
        // SecurityScheme securityScheme = new SecurityScheme()
        //     .type(SecurityScheme.Type.HTTP)
        //     .scheme("bearer")
        //     .bearerFormat("JWT")
        //     .name("Authorization");
        //
        // SecurityRequirement securityRequirement = new SecurityRequirement()
        //     .addList("bearerAuth");

        return new OpenAPI()
            .info(new Info().title("AtChaGong API").version("0.0.1"));
        // .addSecurityItem(securityRequirement)
        // .components(new Components()
        //     .addSecuritySchemes("bearerAuth", securityScheme));
    }
}
