package com.EmployeeMgtSystem.AuthenticationServer.controller;

import com.EmployeeMgtSystem.AuthenticationServer.dto.request.ChangePasswordRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.LoginRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.UserResponse;
import com.EmployeeMgtSystem.AuthenticationServer.exceptions.ErrorDetails;
import com.EmployeeMgtSystem.AuthenticationServer.service.AuthService;
import com.EmployeeMgtSystem.AuthenticationServer.service.UserService;
import com.EmployeeMgtSystem.AuthenticationServer.service.impl.EmployeeClientService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final EmployeeClientService employeeClientService;

    public AuthController(UserService userService, AuthService authService, EmployeeClientService employeeClientService) {
        this.userService = userService;
        this.authService = authService;
        this.employeeClientService = employeeClientService;
    }

    @Operation(summary = "Login User Endpoint", description = "This endpoint allows user login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Hidden
    @GetMapping("/user")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return employeeClientService.getUserByEmail(email);
    }

    @Hidden
    @Operation(summary = "Create user endpoint", security = @SecurityRequirement(name = "bearerAuth"), description = "This endpoint allows user creation (used by employee service)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<BaseResponse> createUser(@Valid @RequestBody CreateUserRequest request, Authentication authentication) {
        System.out.println("auth name: "+authentication.getName());
        return new ResponseEntity<>(authService.createUser(request,authentication),HttpStatus.CREATED);
    }

    @Operation(summary = "Change Password endpoint", security = @SecurityRequirement(name = "bearerAuth"), description = "This endpoint allows users to change their password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PutMapping("/change-password")
    public BaseResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }
}
