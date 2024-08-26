package com.EmployeeMgtSystem.EmployeeService.repository;

import com.EmployeeMgtSystem.EmployeeService.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
    Optional<Employee> findByUserId(long userid);
    Optional<Employee> findByEmail(String email);
}
