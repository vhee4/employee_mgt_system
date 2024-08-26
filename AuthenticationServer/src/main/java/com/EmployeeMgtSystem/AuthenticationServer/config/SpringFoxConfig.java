package com.EmployeeMgtSystem.AuthenticationServer.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Authentication Service",
                description = "Service for authentication",
                version = "v1",
                contact = @Contact(
                        name = "Employee Management System",
                        email = "admin@gmail.com",
                        url = ""
                ),
                license = @License(
                        name = "",
                        url = ""
                ),
                termsOfService = "Terms of service"
        ),
        externalDocs = @ExternalDocumentation(
                description = "",
                url = ""
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Default Server Url"
                ),


        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "bearerAuth",
        description = "Jwt auth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class SpringFoxConfig {

}
