package com.EmployeeMgtSystem.EmployeeService.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
