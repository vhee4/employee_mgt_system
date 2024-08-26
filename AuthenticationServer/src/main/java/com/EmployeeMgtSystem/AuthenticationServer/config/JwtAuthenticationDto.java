package com.EmployeeMgtSystem.AuthenticationServer.config;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class JwtAuthenticationDto {

    private String username;
    private String id;
    private String email;
    private List<String> userRole;
    private List<String> userPermissions;
}
