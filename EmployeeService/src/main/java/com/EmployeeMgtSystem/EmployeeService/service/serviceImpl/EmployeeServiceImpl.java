package com.EmployeeMgtSystem.EmployeeService.service.serviceImpl;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateEmployeeRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.dto.response.EmployeeResponse;
import com.EmployeeMgtSystem.EmployeeService.dto.response.UserResponse;
import com.EmployeeMgtSystem.EmployeeService.enums.EmploymentStatus;
import com.EmployeeMgtSystem.EmployeeService.enums.Status;
import com.EmployeeMgtSystem.EmployeeService.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.EmployeeService.model.Department;
import com.EmployeeMgtSystem.EmployeeService.model.Employee;
import com.EmployeeMgtSystem.EmployeeService.repository.DepartmentRepository;
import com.EmployeeMgtSystem.EmployeeService.repository.EmployeeRepository;
import com.EmployeeMgtSystem.EmployeeService.service.EmployeeService;
import com.EmployeeMgtSystem.EmployeeService.utils.EmailUtils;
import com.EmployeeMgtSystem.EmployeeService.validator.PasswordValidator;
import jakarta.ws.rs.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AuthClientService authClientService;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final PasswordValidator passwordValidator;
    private final EmailUtils emailUtils;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, AuthClientService authClientService, DepartmentRepository departmentRepository, EmailService emailService, PasswordValidator passwordValidator, EmailUtils emailUtils) {
        this.employeeRepository = employeeRepository;
        this.authClientService = authClientService;
        this.departmentRepository = departmentRepository;
        this.emailService = emailService;
        this.passwordValidator = passwordValidator;
        this.emailUtils = emailUtils;
    }

    @Override
    @Transactional
    public Mono<BaseResponse> createEmployee(CreateEmployeeRequest request, String token, Authentication authentication) {

        Employee employee = createEmployeeEntity(request, authentication);
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found. Create a new Department if none exists"));
        return callAuthService(request, token)
                .flatMap(userResponse -> {
                    employee.setUserId(userResponse.getId());
                    employee.setDepartment(department);
                    return Mono.fromCallable(() -> {
                        employeeRepository.save(employee);
                        EmployeeResponse employeeResponse = createEmployeeResponse(employee);
                        try {
                            sendWelcomeEmail(employee, request.getPassword());
                        } catch (BadRequestException e) {
                            throw new BadRequestException("Email sending failed:" + e.getMessage());
                        }
                        return BaseResponse.getResponse("Employee Created successfully", employeeResponse, HttpStatus.CREATED);
                    });
                });
    }

    @Override
    public Mono<BaseResponse> editEmployee(CreateEmployeeRequest request, String token, Authentication authentication) {
        return null;
    }

    public BaseResponse getAllEmployees(String name, String sortBy, String sortOrder, int page, int size) {
        Pageable pageable = createPageRequest(page, size, sortBy, sortOrder);
        Specification<Employee> specification = createSpecification(name);
        Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);

        var response = employeePage.map(this::createEmployeeResponse);
        return BaseResponse.getSuccessfulResponse("Retrieved successfully", response);
    }

    @Override
    public BaseResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return BaseResponse.getResponse("Employee Retrieved successfully", createEmployeeResponse(employee), HttpStatus.OK);
    }

    @Override
    public BaseResponse getEmployeesByDepartment(Long departmentId, int page, int size, String sortBy, String sortOrder) {
        Pageable pageable = createPageRequest(page, size, sortBy, sortOrder);
        Page<Employee> employeesPage = employeeRepository.findByDepartmentId(departmentId, pageable);
        var response = employeesPage.map(this::createEmployeeResponse);
        return BaseResponse.getSuccessfulResponse("Retrieved successfully", response);
    }

    private Pageable createPageRequest(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(sortOrder) ? sort.descending() : sort.ascending();
        return PageRequest.of(page, size, sort);
    }

    private Specification<Employee> createSpecification(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filtering
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + name.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + name.toLowerCase() + "%")
            );
        };
    }

    private Employee createEmployeeEntity(CreateEmployeeRequest request, Authentication authentication) {
        Optional<Employee> optEmployee =  employeeRepository.findByEmail(request.getEmail());
        if(optEmployee.isPresent()){
            throw new BadRequestException("Email already exists");
        }
        boolean validPassword = passwordValidator.validate(request.getPassword());
        if(!validPassword){
            throw new BadRequestException("Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
//                .dateOfBirth(LocalDate.parse(request.getDateOfBirth()))
                .hireDate(LocalDate.parse(request.getHireDate()))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .position(request.getPosition())
                .salary(request.getSalary())
                .build();
        employee.setStatus(Status.ACTIVE);
        employee.setCreatedTime(LocalDateTime.now());
        employee.setCreatedBy(authentication.getName());
        return employee;
    }

    private Mono<UserResponse> callAuthService(CreateEmployeeRequest request, String token) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .password(request.getPassword())
                .build();

        return authClientService.createUser(userRequest, token)
                .flatMap(baseResponse -> {
                    if (baseResponse.getCode() != 200 && baseResponse.getCode() != 201) {
                        return Mono.error(new BadRequestException("Failed to create user: " + baseResponse.getMessage()));
                    }
                    Object data = baseResponse.getData();
                    if (data instanceof LinkedHashMap) {
                        LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;
                        UserResponse userData = new UserResponse();
                        userData.setId(((Number) dataMap.get("id")).longValue());
                        userData.setFirstName((String) dataMap.get("firstName"));
                        userData.setLastName((String) dataMap.get("lastName"));
                        userData.setEmail((String) dataMap.get("email"));
                        userData.setUsername((String) dataMap.get("username"));
                        userData.setStatus(String.valueOf(Status.valueOf((String) dataMap.get("status"))));
                        userData.setCreatedBy((String) dataMap.get("createdBy"));
                        userData.setCreatedTime(LocalDateTime.parse((String) dataMap.get("createdTime")));
                        userData.setRoles((List<String>) dataMap.get("roles"));
                        return Mono.just(userData);
                    } else {
                        return Mono.error(new BadRequestException("Unexpected response data type"));
                    }
                });
    }

    private EmployeeResponse createEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getUserId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getMiddleName() == null ? null : employee.getMiddleName(),
                employee.getDepartment().getId(),
                employee.getEmail(),
                employee.getPhoneNumber() == null ? null : employee.getPhoneNumber(),
//                employee.getDateOfBirth() == null ? null : employee.getDateOfBirth(),
                employee.getHireDate() == null ? null : employee.getHireDate(),
                employee.getEmploymentStatus() == null ? null : employee.getEmploymentStatus().name(),
                employee.getPosition() == null ? null : employee.getPosition(),
                employee.getSalary() == null ? null : employee.getSalary(),
                employee.getCreatedBy(),
                employee.getStatus(),
                employee.getCreatedTime(),
                employee.getUpdatedTime()
        );
    }

    private boolean isValidDateOfBirth(String dateOfBirthStr) {
        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
            System.out.println("Date is null or empty");
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth;

        try {
            dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter);
        } catch (DateTimeParseException e) {
            // Handle parsing error
            System.err.println("Date parsing error: " + e.getMessage());
            return false;
        }
        LocalDate now = LocalDate.now();
        LocalDate tenYearsAgo = now.minusYears(10);
        System.out.println(tenYearsAgo);
        System.out.println("Date does not match expextations");


//        return !dateOfBirth.isAfter(now) && !dateOfBirth.isBefore(tenYearsAgo);
        return  true;
    }

    private void sendWelcomeEmail(Employee employee, String temporaryPassword) {
        String subject = "Welcome to the Company!";
        String text =emailUtils.welcomeMessage(employee,temporaryPassword);
        emailService.sendEmail(employee.getEmail(), subject, text);
    }

}