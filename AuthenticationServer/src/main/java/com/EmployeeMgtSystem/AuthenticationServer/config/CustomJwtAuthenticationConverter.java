package com.EmployeeMgtSystem.AuthenticationServer.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
    public class CustomJwtAuthenticationConverter implements Converter<Jwt, CustomAuthentication> {

        @Override
        public CustomAuthentication convert(Jwt source) {
            Map<String, Object> claims = source.getClaims();
            Collection<String> authorities = (List<String>) claims.get("userRole");

            JwtAuthenticationDto jwtAuthenticationDto = fromJwt(claims);

            return new CustomAuthentication(source, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()), jwtAuthenticationDto.getUsername(), jwtAuthenticationDto);
        }

        private JwtAuthenticationDto fromJwt(Map<String, Object> claims){

            return JwtAuthenticationDto.builder()
                    .userRole(Collections.singletonList(String.valueOf(claims.get("userRole"))))
                    .email(String.valueOf(claims.get("email")))
                    .id(String.valueOf(claims.get("id")))
                    .username(String.valueOf(claims.get("username")))
                    .userPermissions((List<String>) claims.get("userPermissions"))
                    .build();
        }
}
