package com.EmployeeMgtSystem.EmployeeService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${public-key}")
    private String public_Key;

    @Value("${issuer}")
    private String issuer;
    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    @Bean
    SecurityFilterChain springsecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger/index.html","/swagger-ui.html","/swagger/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/swagger-resources", "/api-docs/**").permitAll()
                        .anyRequest().permitAll())
                .sessionManagement(Customizer.withDefaults())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> {
                    csrf.disable();
                    csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                })
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("Authentication error: " + authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            try {
                                jwt
                                        .decoder(jwtDecoder())
                                        .jwtAuthenticationConverter(customJwtAuthenticationConverter);

                            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                                throw new RuntimeException(e);
                            }
                        }));

        return http.build();
    }


    public RSAPublicKey getPublicKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Read file content directly using absolute path
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFile));
        String publicKeyStr = new String(keyBytes, StandardCharsets.UTF_8);

        // Parse PEM object
        PemObject pemObject = new PemReader(new StringReader(publicKeyStr)).readPemObject();
        byte[] der = pemObject.getContent();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.addAllowedOrigin("**");
        configuration.addAllowedHeader("**");
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKey publicKey = getPublicKeyFromFile(public_Key);

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey)
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();

        // Validate the JWT issuer
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);

        // Optionally add more validators, such as for audience, timestamps, etc.
        OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withTimestamp));

        return jwtDecoder;
    }


}
