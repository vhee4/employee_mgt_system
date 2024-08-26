package com.EmployeeMgtSystem.EmployeeService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeRequest {
    @NotNull(message = "please pass in first name")
    @NotEmpty(message = "please pass in first name")
    private String firstName;
    @NotEmpty(message = "please pass in last name")
    @NotNull(message = "please pass in last name")
    private String lastName;
    private String middleName;
    @NotNull(message = "please pass in email")
    @NotEmpty(message = "please pass in email")
    @Email(message = "Email format is invalid")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email should have a valid domain")
    private String email;
    @NotNull(message = "please pass in role")
    @NotEmpty(message = "please pass in role")
    private String role;
    @NotNull(message = "please pass in department id")
    private int departmentId;
    @NotNull(message = "please pass in phone number")
    @NotEmpty(message = "please pass in phone number")
    private String phoneNumber;
    @NotNull(message = "please pass in hire date")
    @NotEmpty(message = "please pass in hire date")
    private String hireDate;
    private String position;
    @NotNull(message = "please pass in salary")
    private BigDecimal salary;
}
