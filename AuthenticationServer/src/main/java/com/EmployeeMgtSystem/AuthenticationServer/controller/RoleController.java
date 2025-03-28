package com.EmployeeMgtSystem.AuthenticationServer.controller;

import com.EmployeeMgtSystem.AuthenticationServer.config.CustomUserDetails;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.AssignAndUnassignRolesRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateRoleRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public BaseResponse createRole(CreateRoleRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        return roleService.createRole(request,user.getName());
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable int id, @RequestBody Role role) {
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/{roleId}")
    public void deleteRole(@PathVariable int roleId) {
        roleService.deleteRole(roleId);
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping("/assign-permissions")
    public BaseResponse assignPermissionsToRole(@RequestBody AssignAndUnassignRolesRequest request) {
        return roleService.assignPermissionsToRole(request);
    }

    @PostMapping("/unassign-permissions")
    public BaseResponse unassignPermissionsFromRole(@RequestBody AssignAndUnassignRolesRequest request) {
        return roleService.unAssignPermissionsFromRole(request);
    }
}

