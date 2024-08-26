package com.EmployeeMgtSystem.EmployeeService.dto.request;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String role;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
