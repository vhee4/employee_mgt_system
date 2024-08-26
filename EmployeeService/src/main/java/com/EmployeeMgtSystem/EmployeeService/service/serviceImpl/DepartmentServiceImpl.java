package com.EmployeeMgtSystem.EmployeeService.service.serviceImpl;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateDepartmentRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.dto.response.DepartmentResponse;
import com.EmployeeMgtSystem.EmployeeService.dto.response.PaginatedResponse;
import com.EmployeeMgtSystem.EmployeeService.enums.Status;
import com.EmployeeMgtSystem.EmployeeService.exceptions.ResourceAlreadyExistException;
import com.EmployeeMgtSystem.EmployeeService.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.EmployeeService.model.Department;
import com.EmployeeMgtSystem.EmployeeService.model.Employee;
import com.EmployeeMgtSystem.EmployeeService.repository.DepartmentRepository;
import com.EmployeeMgtSystem.EmployeeService.repository.EmployeeRepository;
import com.EmployeeMgtSystem.EmployeeService.service.DepartmentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public BaseResponse createDepartment(CreateDepartmentRequest request, Authentication authentication) {
        Optional<Department> existingDept = departmentRepository.findByName(request.getName());
        if (existingDept.isPresent()) {
            throw new ResourceAlreadyExistException("Department name already exists");
        }
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        department.setCreatedBy(authentication.getName());
        department.setCreatedTime(LocalDateTime.now());
        department.setStatus(Status.ACTIVE);
        departmentRepository.save(department);
        DepartmentResponse response = getDepartmentResponse(department);
        return BaseResponse.getResponse("Department created successfully", response, HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public BaseResponse editDepartment(int id, CreateDepartmentRequest request, Authentication authentication) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (departmentRepository.findByName(request.getName()).isPresent() &&
                !department.getName().equals(request.getName())) {
            throw new ResourceAlreadyExistException("Department name already exists");
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setUpdatedBy(authentication.getName());
        department.setUpdatedTime(LocalDateTime.now());

        departmentRepository.save(department);
        DepartmentResponse response = getDepartmentResponse(department);
        return BaseResponse.getResponse("Department updated successfully", response, HttpStatus.OK);
    }

    @Override
    public BaseResponse getAllDepartments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Department> departmentPage = departmentRepository.findAll(pageable);
        List<DepartmentResponse> responses = departmentPage.getContent().stream()
                .map(DepartmentServiceImpl::getDepartmentResponse)
                .collect(Collectors.toList());

        return BaseResponse.getSuccessfulResponse("All departments fetched successfully",
                new PaginatedResponse<>(responses, departmentPage.getNumber(), departmentPage.getSize(), departmentPage.getTotalElements(), departmentPage.getTotalPages()));
    }

    @Override
    public BaseResponse getDepartmentById(int id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        DepartmentResponse response = getDepartmentResponse(department);
        return BaseResponse.getResponse("Department fetched successfully", response, HttpStatus.OK);
    }

    private static DepartmentResponse getDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
                .departmentId(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }
}
