package com.EmployeeMgtSystem.AuthenticationServer.controller;

import com.EmployeeMgtSystem.AuthenticationServer.dto.request.ChangePasswordRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.LoginRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.service.AuthService;
import com.EmployeeMgtSystem.AuthenticationServer.service.UserService;
import com.EmployeeMgtSystem.AuthenticationServer.service.impl.EmployeeClientService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private EmployeeClientService employeeClientService;

    private LoginRequest loginRequest;
    private CreateUserRequest createUserRequest;
    private ChangePasswordRequest changePasswordRequest;
    String adminToken;
        String firstName = "FirstName-" + System.currentTimeMillis();
        String lastName = "LastName-" + System.currentTimeMillis();
        String email = "email-" + System.currentTimeMillis() + "@gmail.com";
    @BeforeEach
    void setUp() throws Exception {
        // Setup initial data
        loginRequest = new LoginRequest("admin@gmail.com", "Admin123!");
        createUserRequest = new CreateUserRequest("ADMIN",email, "password123", firstName, lastName);
        changePasswordRequest = new ChangePasswordRequest("oldPassword123", "newPassword123", email);

        // Perform login with admin credentials
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@gmail.com\",\"password\":\"Admin123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the token from the response (assuming the token is in "data.token")
        String token = JsonPath.parse(loginResponse).read("$.data.token");

        // Store the token for use in other tests
        this.adminToken = "Bearer " + token;
    }


    @Test
    void login_ValidCredentials_ReturnsOk() throws Exception {
        BaseResponse baseResponse = new BaseResponse(HttpStatus.OK,200,"Login successful");
        Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenReturn(baseResponse);

       var result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\""+email+"\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createUser_ValidRequest_ReturnsCreated() throws Exception {
        BaseResponse baseResponse = new BaseResponse(HttpStatus.CREATED,201,"User created successfully");
        Mockito.when(authService.createUser(Mockito.any(CreateUserRequest.class), Mockito.any())).thenReturn(baseResponse);

        mockMvc.perform(post("/api/v1/auth/create-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                        .content("{\"email\":\""+email+"\",\"password\":\"password123\",\"role\":\"EMPLOYEE\",\"firstName\":\""+firstName+"\",\"lastName\":\""+lastName+"\",\"phoneNumber\":\"123-456-7890\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.code").value(201));
    }

    @Test
    void changePassword_ValidRequest_ReturnsOk() throws Exception {
        BaseResponse baseResponse = new BaseResponse(HttpStatus.OK,200,"Password changed successfully");
        Mockito.when(authService.changePassword(Mockito.any(ChangePasswordRequest.class))).thenReturn(baseResponse);

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                        .content("{\"oldPassword\":\"oldPassword123\",\"newPassword\":\"newPassword123\",\"email\":\""+email+"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.code").value(200));
    }

}
