package com.EmployeeMgtSystem.AuthenticationServer.service;

import com.EmployeeMgtSystem.AuthenticationServer.model.Role;

import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    Role updateRole(int id, Role role);
    void deleteRole(int id);
    List<Role> getAllRoles();
    void assignPermissionsToRole(int roleId, List<Integer> permissionIds);


    }
