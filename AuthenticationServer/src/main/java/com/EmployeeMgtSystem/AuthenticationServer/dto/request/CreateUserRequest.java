package com.EmployeeMgtSystem.AuthenticationServer.dto.request;


import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CreateUserRequest implements Serializable {
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
