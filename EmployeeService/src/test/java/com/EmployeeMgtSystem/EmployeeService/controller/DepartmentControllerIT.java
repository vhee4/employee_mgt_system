package com.EmployeeMgtSystem.EmployeeService.controller;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateDepartmentRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.model.Department;
import com.EmployeeMgtSystem.EmployeeService.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;
    private String accessToken;


    @BeforeEach
    void setUp() throws Exception {
        loginUser("admin@gmail.com", "Admin123!");
//        departmentRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createDepartment_ShouldReturn201_WhenDepartmentIsCreated() throws Exception {
        // Arrange
        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("Engineering");
        request.setDescription("Engineering Department");

        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Department created successfully"))
                .andReturn();

        // Assert that the department was created
        String responseContent = result.getResponse().getContentAsString();
        BaseResponse response = objectMapper.readValue(responseContent, BaseResponse.class);
        assertThat(response.getMessage()).isEqualTo("Department created successfully");

        // Validate that the department is saved in the database
        Department department = departmentRepository.findByName("Engineering").orElse(null);
        assertThat(department).isNotNull();
        assertThat(department.getName()).isEqualTo("Engineering");
        assertThat(department.getDescription()).isEqualTo("Engineering Department");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateDepartment_ShouldReturn200_WhenDepartmentIsUpdated() throws Exception {
        // Arrange
        Department department = new Department();
        department.setName("HR");
        department.setDescription("Human Resources Department");
        departmentRepository.save(department);

        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("Human Resources");
        request.setDescription("Updated Human Resources Department");

        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(put("/api/v1/department/{id}", department.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Department updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Human Resources"))
                .andExpect(jsonPath("$.data.description").value("Updated Human Resources Department"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllDepartments_ShouldReturn200_WithAllDepartments() throws Exception {
        // Arrange
        Department department1 = new Department();
        department1.setName("Finance");
        department1.setDescription("Finance Department");
        departmentRepository.save(department1);

        Department department2 = new Department();
        department2.setName("IT");
        department2.setDescription("IT Department");
        departmentRepository.save(department2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/department")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All departments fetched successfully"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getDepartmentById_ShouldReturn200_WhenDepartmentExists() throws Exception {
        // Arrange
        Department department = new Department();
        department.setName("Marketing");
        department.setDescription("Marketing Department");
        departmentRepository.save(department);

        // Act & Assert
        mockMvc.perform(get("/api/v1/department/"+ department.getId())
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Department fetched successfully"))
                .andExpect(jsonPath("$.data.name").value("Marketing"))
                .andExpect(jsonPath("$.data.description").value("Marketing Department"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getDepartmentById_ShouldReturn404_WhenDepartmentDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/department/{id}", 9999L)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Department not found"));
    }

    private void loginUser(String email, String password) throws Exception {
        String authRequest = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(jakarta.ws.rs.core.MediaType.APPLICATION_JSON));

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
                        .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                        .content(createDeptRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
