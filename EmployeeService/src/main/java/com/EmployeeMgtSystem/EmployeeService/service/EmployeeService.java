package com.EmployeeMgtSystem.EmployeeService.service;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateEmployeeRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.dto.response.UserResponse;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmployeeService {
    Mono<BaseResponse> createEmployee(CreateEmployeeRequest request, String token, Authentication authentication);

    Mono<BaseResponse> editEmployee(CreateEmployeeRequest request, String token, Authentication authentication);

    BaseResponse getAllEmployees(String name, String sortBy, String sortOrder, int page, int size);

    BaseResponse getEmployeeById(Long id);

    BaseResponse getEmployeesByDepartment(Long departmentId, int page, int size, String sortBy, String sortOrder);
}
