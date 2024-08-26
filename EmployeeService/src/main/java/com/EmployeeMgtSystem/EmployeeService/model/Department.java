package com.EmployeeMgtSystem.EmployeeService.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "department")
public class Department extends BaseEntity<Integer>{
    private String name;
    private String description;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
