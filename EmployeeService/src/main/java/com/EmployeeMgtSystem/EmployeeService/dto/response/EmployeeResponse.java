package com.EmployeeMgtSystem.EmployeeService.dto.response;

import com.EmployeeMgtSystem.EmployeeService.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private Long employeeId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private Integer departmentId;
    private String email;
    private String phoneNumber;
//    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private String employmentStatus;
    private String position;
    private BigDecimal salary;
    private String createdBy;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
