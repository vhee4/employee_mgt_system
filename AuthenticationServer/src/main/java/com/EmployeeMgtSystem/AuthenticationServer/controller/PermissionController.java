package com.EmployeeMgtSystem.AuthenticationServer.controller;

import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import com.EmployeeMgtSystem.AuthenticationServer.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @PostMapping
    public Permission createPermission(@RequestBody Permission permission) {
        return permissionService.createPermission(permission);
    }

    @PutMapping("/{id}")
    public Permission updatePermission(@PathVariable int id, @RequestBody Permission permission) {
        return permissionService.updatePermission(id, permission);
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable int id) {
        permissionService.deletePermission(id);
    }

    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
}

