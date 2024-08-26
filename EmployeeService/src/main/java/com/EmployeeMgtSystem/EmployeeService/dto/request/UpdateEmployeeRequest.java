package com.EmployeeMgtSystem.EmployeeService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeRequest {
    private Long employeeId;

    private int departmentId;
    private String phoneNumber;
    private String employmentStatus;
    private String hireDate;
    private String position;
    private BigDecimal salary;
}
