package com.EmployeeMgtSystem.EmployeeService.model;

import com.EmployeeMgtSystem.EmployeeService.enums.EmploymentStatus;
import com.EmployeeMgtSystem.EmployeeService.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends BaseEntity<Long> {
    private String firstName;
    private String lastName;
    private String middleName;
    private Long userId;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;
    private String position;
    private BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
