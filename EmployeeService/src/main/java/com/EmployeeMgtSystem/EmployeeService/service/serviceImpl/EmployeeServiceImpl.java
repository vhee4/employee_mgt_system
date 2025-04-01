package com.EmployeeMgtSystem.EmployeeService.service.serviceImpl;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateEmployeeRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.request.UpdateEmployeeRequest;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AuthClientService authClientService;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final PasswordValidator passwordValidator;
    private final EmailUtils emailUtils;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


//    public EmployeeServiceImpl(EmployeeRepository employeeRepository, AuthClientService authClientService, DepartmentRepository departmentRepository, EmailService emailService, PasswordValidator passwordValidator, EmailUtils emailUtils) {
//        this.employeeRepository = employeeRepository;
//        this.authClientService = authClientService;
//        this.departmentRepository = departmentRepository;
//        this.emailService = emailService;
//        this.passwordValidator = passwordValidator;
//        this.emailUtils = emailUtils;
//    }

//    @Override
//    @Transactional
//    public Mono<BaseResponse> createEmployee(CreateEmployeeRequest request, String token, Authentication authentication) {
//        String password = generatePassword();
//        Employee employee = createEmployeeEntity(request, authentication,password);
//        Department department = departmentRepository.findById(request.getDepartmentId())
//                .orElseThrow(() -> new ResourceNotFoundException("Department not found. Create a new Department if none exists"));
//        return callAuthService(request, token,password)
//                .flatMap(userResponse -> {
//                    employee.setUserId(userResponse.getId());
//                    employee.setDepartment(department);
//                    return Mono.fromCallable(() -> {
//                        employeeRepository.save(employee);
//                        EmployeeResponse employeeResponse = createEmployeeResponse(employee);
//                        try {
//                            sendWelcomeEmail(employee, password);
//                        } catch (BadRequestException e) {
//                            throw new BadRequestException("Email sending failed:" + e.getMessage());
//                        }
//                        return BaseResponse.getResponse("Employee Created successfully", employeeResponse, HttpStatus.CREATED);
//                    });
//                });
//    }

    @Override
    @Transactional
    public Mono<BaseResponse> createEmployee(CreateEmployeeRequest request, String token, Authentication authentication) {
        String password = generatePassword();
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found. Create a new Department if none exists"));

        Employee employee = createEmployeeEntity(request, authentication, password, department);

        CreateUserRequest userRequest = createUserRequest(request, password);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonRequest = objectMapper.writeValueAsString(userRequest); // Convert DTO to JSON
            System.out.println("sending message to auth service");
            System.out.println("UserRequest JSON: " + jsonRequest);
            rabbitTemplate.convertAndSend("user_creation_exchange","user.create.request", jsonRequest);
//                MessageProperties props = message.getMessageProperties();
//                props.setHeader("Authorization", "Bearer "+token);
//                return message;
//            });
            System.out.println("message sent successfully to auth service");


        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CreateUserRequest", e);
        }

        employeeRepository.save(employee);

        EmployeeResponse employeeResponse = createEmployeeResponse(employee);
        return Mono.just(BaseResponse.getResponse("Employee Created successfully, An email would be sent shortly", employeeResponse, HttpStatus.CREATED));

    }

    @RabbitListener(queues = "employee.user.create.response.queue")
    public void handleUserCreationResponse(Message message) {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            System.out.println("Routing Key: " + routingKey);
            if(!routingKey.equalsIgnoreCase("user.create.response")){
                return;
            }
            // Extract headers
//            Map<String, Object> headers = message.getMessageProperties().getHeaders();
//            String correlationId = (String) headers.get("correlationId");
//            String requestId = (String) headers.get("requestId");

            // Deserialize message body
            String body = new String(message.getBody());
            System.out.println("Received message from auth: "+body);

            BaseResponse response = objectMapper.readValue(body, BaseResponse.class);

            if (response == null || response.getData() == null) {
                System.out.println("Invalid response received for user creation");
                return;
            }

            UserResponse createdUser = objectMapper.convertValue(response.getData(), UserResponse.class);

            // Find the employee and update `userId`
            Optional<Employee> optionalEmployee = employeeRepository.findByEmail(createdUser.getEmail());
            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                employee.setUserId(createdUser.getId());

                System.out.println("Updated employee with userId: " + createdUser.getId());

                // Send welcome email
                try {
                    sendWelcomeEmail(createdUser.getFirstName(), createdUser.getLastName(),createdUser.getEmail(), createdUser.getPassword());
                } catch (BadRequestException e) {
                    System.err.println("Email sending failed: " + e.getMessage());
                }
                employeeRepository.save(employee);
            } else {
                System.out.println("Employee record not found for email: " + createdUser.getEmail());
            }

        } catch (Exception e) {
            System.err.println("Failed to process user creation response: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public BaseResponse editEmployee(UpdateEmployeeRequest request, String token, Authentication authentication) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found"));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found"));

        employee.setDepartment(department);
        employee.setEmploymentStatus(EmploymentStatus.valueOf(request.getEmploymentStatus()));
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setHireDate(LocalDate.parse(request.getHireDate()));
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        employee.setUpdatedTime(LocalDateTime.now());
        employee.setUpdatedBy(authentication.getName());

        employeeRepository.save(employee);
        return BaseResponse.getSuccessfulResponse("Employee updated successfully",createEmployeeResponse(employee));

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
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
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

    private Employee createEmployeeEntity(CreateEmployeeRequest request, Authentication authentication,String password,Department department) {
        Optional<Employee> optEmployee =  employeeRepository.findByEmail(request.getEmail());
        if(optEmployee.isPresent()){
            throw new BadRequestException("Email already exists");
        }
        boolean validPassword = passwordValidator.validate(password);
        if(!validPassword){
            throw new BadRequestException("Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .hireDate(LocalDate.parse(request.getHireDate()))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .position(request.getPosition())
                .salary(request.getSalary())
                .department(department)
                .build();
        employee.setStatus(Status.ACTIVE);
        employee.setCreatedTime(LocalDateTime.now());
        employee.setCreatedBy(authentication.getName());
        return employee;
    }

    private CreateUserRequest createUserRequest(CreateEmployeeRequest request, String password) {
        return CreateUserRequest.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .password(password)
                .build();
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
                employee.getHireDate() == null ? null : employee.getHireDate(),
                employee.getEmploymentStatus() == null ? null : employee.getEmploymentStatus().name(),
                employee.getPosition() == null ? null : employee.getPosition(),
                employee.getSalary() == null ? null : employee.getSalary(),
                employee.getCreatedBy(),
                employee.getUpdatedBy(),
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

    private void sendWelcomeEmail(String firstname, String lastname, String email, String temporaryPassword) {
        System.out.println("sending email");
        String subject = "Welcome to the Company!";
        String text =emailUtils.welcomeMessage(firstname,lastname,email,temporaryPassword);
        emailService.sendEmail(email, subject, text);
    }

    private String generatePassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialCharacters = "!@#$%^&*";

        Random random = new Random();
        StringBuilder password = new StringBuilder(8);

        // Ensure password has at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        // Fill the rest of the password with random characters from all categories
        String allCharacters = upperCase + lowerCase + digits + specialCharacters;
        for (int i = password.length(); i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // Shuffle the password to ensure randomness
        List<Character> passwordList = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordList.add(c);
        }
        Collections.shuffle(passwordList);

        // Convert the list back to a string
        StringBuilder finalPassword = new StringBuilder();
        for (char c : passwordList) {
            finalPassword.append(c);
        }

        System.out.println("Password generated: " + finalPassword);
        return finalPassword.toString();
    }


}