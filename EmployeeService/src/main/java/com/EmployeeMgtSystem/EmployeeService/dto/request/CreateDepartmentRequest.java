package com.EmployeeMgtSystem.EmployeeService.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDepartmentRequest {
    @NotNull(message = "please pass in department name")
    @NotEmpty(message = "please pass in department name")
    private String name;
    @NotNull(message = "please pass in department description")
    @NotEmpty(message = "please pass in department description")
    private String description;
}
