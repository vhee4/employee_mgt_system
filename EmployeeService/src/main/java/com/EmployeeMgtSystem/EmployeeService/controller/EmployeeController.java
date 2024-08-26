package com.EmployeeMgtSystem.EmployeeService.controller;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateEmployeeRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.request.UpdateEmployeeRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.exceptions.ErrorDetails;
import com.EmployeeMgtSystem.EmployeeService.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(
        name = "Rest APIS for Employee Management",
        description = "Create, Read, Update, Delete APIs"
)
@RestController
@RequestMapping("/api/v1/employee")
@Validated
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create Employee Endpoint", description = "This endpoint allows employee creation (Restricted to admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP STATUS CREATED"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Mono<BaseResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request, HttpServletRequest httpRequest,
                                                            Authentication authentication) {
        String bearerToken = httpRequest.getHeader("Authorization");
        System.out.println("Bearer token:" + bearerToken);
        return new ResponseEntity<>(employeeService.createEmployee(request, bearerToken, authentication), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Employee Endpoint", description = "This endpoint allows employee details to be updated (Restricted to admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse> updateEmployee(@RequestBody UpdateEmployeeRequest request, HttpServletRequest httpRequest,
                                                       Authentication authentication) {
        String bearerToken = httpRequest.getHeader("Authorization");
        System.out.println("Bearer token:" + bearerToken);
        return new ResponseEntity<>(employeeService.editEmployee(request, bearerToken, authentication), HttpStatus.CREATED);
    }


    @Operation(summary = "Get Employee Endpoint", description = "This endpoint allows retrieval of all employees (Restricted to admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("get-all-employees")
    public BaseResponse getAllEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return employeeService.getAllEmployees(name, sortBy, sortOrder, page, size);
    }

    @Operation(summary = "Get Employee by Id Endpoint", description = "This endpoint allows retrieval of an employee by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })
    @GetMapping("get-employee/{id}")
    public BaseResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @Operation(summary = "Get Employee by Dept Endpoint", description = "This endpoint allows retrieval of all employees ina  department (Restricted to admins and managers)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @GetMapping("get-employees-by-dept/{departmentId}")
    public BaseResponse getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return employeeService.getEmployeesByDepartment(departmentId, page, size, sortBy, sortOrder);
    }
}
