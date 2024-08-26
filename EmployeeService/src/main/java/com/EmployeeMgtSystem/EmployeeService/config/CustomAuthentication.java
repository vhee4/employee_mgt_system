package com.EmployeeMgtSystem.EmployeeService.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

@Getter
public class CustomAuthentication extends JwtAuthenticationToken {

    private final JwtAuthenticationDto jwtAuthenticationDto;

    public CustomAuthentication(
            Jwt jwt,
            Collection<? extends GrantedAuthority> authorities,
            String name,
            JwtAuthenticationDto jwtAuthenticationDto) {
        super(jwt, authorities, name);
        this.jwtAuthenticationDto=jwtAuthenticationDto;
    }
}
