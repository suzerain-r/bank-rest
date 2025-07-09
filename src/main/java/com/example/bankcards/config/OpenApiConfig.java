package com.example.bankcards.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Card Service API")
                        .version("1.0.0")
                        .description("API for managing bank cards")
                        .contact(new Contact()
                                .name("Rassul Saduakas")
                                .email("rassaduakas01@gmail.com")
                                .url("https://github.com/suzerain-r")));
    }
}

