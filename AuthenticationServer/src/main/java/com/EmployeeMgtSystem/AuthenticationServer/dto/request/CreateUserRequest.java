package com.EmployeeMgtSystem.AuthenticationServer.dto.request;


import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NonNull
    private String role;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
}
