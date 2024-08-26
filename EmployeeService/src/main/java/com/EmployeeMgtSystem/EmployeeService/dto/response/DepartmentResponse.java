package com.EmployeeMgtSystem.EmployeeService.dto.response;

import com.EmployeeMgtSystem.EmployeeService.model.Employee;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {
    private int departmentId;
    private String name;
    private String description;
}
