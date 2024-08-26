package com.EmployeeMgtSystem.AuthenticationServer.controller;

import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable int id, @RequestBody Role role) {
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable int id) {
        roleService.deleteRole(id);
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping("/{roleId}/permissions")
    public void assignPermissionsToRole(@PathVariable int roleId, @RequestBody List<Integer> permissionIds) {
        roleService.assignPermissionsToRole(roleId, permissionIds);
    }
}

