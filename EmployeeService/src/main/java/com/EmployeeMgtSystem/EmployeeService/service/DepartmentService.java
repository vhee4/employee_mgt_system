package com.EmployeeMgtSystem.EmployeeService.service;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateDepartmentRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface DepartmentService {
    BaseResponse createDepartment(CreateDepartmentRequest request,Authentication authentication);
    BaseResponse editDepartment(Long id, CreateDepartmentRequest request, Authentication authentication);
    BaseResponse getAllDepartments(int page, int size);
    BaseResponse getDepartmentById(Long id);
    }
