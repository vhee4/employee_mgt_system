package com.EmployeeMgtSystem.EmployeeService.controller;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateEmployeeRequest;
import com.EmployeeMgtSystem.EmployeeService.service.EmployeeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerIT {

    private final MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EmployeeService employeeService;
    private String accessToken;

    @Autowired
    public EmployeeControllerIT(MockMvc mockMvc, ObjectMapper objectMapper, EmployeeService employeeService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.employeeService = employeeService;
    }

    @BeforeEach
    void setUp() throws Exception {
        loginUser("admin@gmail.com", "Admin123!");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createEmployee_ShouldReturn201_WhenEmployeeIsCreated() throws Exception {
        createEmployee("EMPLOYEE");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllEmployees_ShouldReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/get-all-employees")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortOrder", "asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Retrieved successfully"));
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    void getEmployeeById_ShouldReturn200_WhenEmployeeExists() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/get-employee/{id}", employeeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employee Retrieved successfully"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getEmployeeById_ShouldReturn404_WhenEmployeeDoesNotExist() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/get-employee/{id}", nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employee not found"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getEmployeesByDepartment_ShouldReturn200() throws Exception {
        Long departmentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/get-employees-by-dept/{departmentId}", departmentId)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortOrder", "asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Retrieved successfully"));
    }

    private void createEmployee(String role) throws Exception {
        String firstName = "FirstName-" + System.currentTimeMillis();
        String lastName = "LastName-" + System.currentTimeMillis();
        String email = "email-" + System.currentTimeMillis() + "@gmail.com";

        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPhoneNumber("08012345678");
        request.setHireDate("2024-08-25");
        request.setPosition("Developer");
        request.setSalary(new BigDecimal("50000"));
        request.setDepartmentId(1);
        request.setRole(role);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    private void loginUser(String email, String password) throws Exception {
        String authRequest = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<String> authResponse = restTemplate.exchange(
                "http://localhost:8080/api/v1/auth/login", // URL of the auth service
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(authResponse.getBody());
        String token = jsonNode.path("data").path("token").asText();

        accessToken = "Bearer " + token;

        String departmentName = "Department-" + System.currentTimeMillis();
        String createDeptRequest = "{ \"name\": \"" + departmentName + "\", \"description\": \"Test description\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/department")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createDeptRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
