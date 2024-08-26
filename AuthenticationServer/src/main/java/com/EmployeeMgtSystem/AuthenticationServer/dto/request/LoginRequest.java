package com.EmployeeMgtSystem.AuthenticationServer.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "please pass in email")
    @NotEmpty(message = "please pass in email")
    private String email;
    @NotNull(message = "please pass in password")
    @NotEmpty(message = "please pass in password")
    private String password;
}
