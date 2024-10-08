package com.EmployeeMgtSystem.EmployeeService.repository;

import com.EmployeeMgtSystem.EmployeeService.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Integer> {
    Optional<Department> findByName(String name);
}
