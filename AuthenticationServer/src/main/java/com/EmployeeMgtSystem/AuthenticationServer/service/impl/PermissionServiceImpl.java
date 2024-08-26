package com.EmployeeMgtSystem.AuthenticationServer.service.impl;

import com.EmployeeMgtSystem.AuthenticationServer.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import com.EmployeeMgtSystem.AuthenticationServer.repository.PermissionRepository;
import com.EmployeeMgtSystem.AuthenticationServer.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission updatePermission(int id, Permission permission) {
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        existingPermission.setName(permission.getName());
        existingPermission.setDescription(permission.getDescription());
        return permissionRepository.save(existingPermission);
    }

    public void deletePermission(int id) {
        permissionRepository.deleteById(id);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
