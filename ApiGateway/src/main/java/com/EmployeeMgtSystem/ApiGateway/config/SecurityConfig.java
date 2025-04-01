package com.EmployeeMgtSystem.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyExchange().authenticated()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
