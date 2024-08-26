package com.EmployeeMgtSystem.ApiGateway.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@EnableWebSecurity
@Configuration
@AllArgsConstructor
public class SecurityConfig {
    @Bean
    SecurityFilterChain springsecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login","/swagger/index.html","/swagger-ui.html","/swagger/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/swagger-resources", "/api-docs/**").permitAll()
                        .anyRequest().permitAll())
                .sessionManagement(Customizer.withDefaults())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Allow all requests to pass through
                .csrf(csrf->{csrf.disable();csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());});  // Disable CSRF as the API Gateway doesn't handle forms

        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowedOrigins(List.of("http://localhost:8081","http://localhost:8080","http://localhost:8081/swagger-ui.html","http://localhost:8081/swagger-ui/index.html","http://localhost:8081/","/swagger/index.html","/swagger-ui.html","/swagger/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/swagger-resources", "/api-docs/**")); // Replace with your Swagger UI URL
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
