package com.EmployeeMgtSystem.EmployeeService.controller;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateDepartmentRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.exceptions.ErrorDetails;
import com.EmployeeMgtSystem.EmployeeService.service.DepartmentService;
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


@Tag(
        name = "Rest APIS for Department Management",
        description = "Create, Read, Update, Delete APIs"
)
@RestController
@RequestMapping("/api/v1/department")
@Validated
public class DepartmentController {
    private DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Operation(summary = "Create Department Endpoint", description = "This endpoint allows department creation (Restricted to admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP STATUS CREATED"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request, HttpServletRequest httpRequest,
                                                         Authentication authentication){
        return new ResponseEntity<>(departmentService.createDepartment(request,authentication), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Department Endpoint", description = "This endpoint allows department details update (Restricted to admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "HTTP STATUS BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateDepartment(@PathVariable Long id, @Valid @RequestBody CreateDepartmentRequest request, Authentication authentication) {
        BaseResponse response = departmentService.editDepartment(id, request, authentication);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @Operation(summary = "Get all Departments Endpoint", description = "This endpoint allows retrieval of all departments (Restricted to admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BaseResponse response = departmentService.getAllDepartments(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @Operation(summary = "Get Department by Id Endpoint", description = "This endpoint allows retrieval of a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP STATUS OK"),
            @ApiResponse(responseCode = "404", description = "HTTP STATUS NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getDepartmentById(@PathVariable Long id) {
        BaseResponse response = departmentService.getDepartmentById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
