package com.certifolio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI certifolioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CertiFolio API")
                        .description("Student Skill & Certification Portfolio Registry REST API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CertiFolio Academic Registry")
                                .email("registry@certifolio.edu"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
