package com.EmployeeMgtSystem.AuthenticationServer.service;

import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;

import java.util.List;

public interface PermissionService {
    Permission createPermission(Permission permission);
    Permission updatePermission(int id, Permission permission);
    void deletePermission(int id);
    List<Permission> getAllPermissions();

}
