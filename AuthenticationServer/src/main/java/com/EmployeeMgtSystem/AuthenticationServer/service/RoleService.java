package com.EmployeeMgtSystem.AuthenticationServer.service;

import com.EmployeeMgtSystem.AuthenticationServer.dto.request.AssignAndUnassignRolesRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateRoleRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.model.Role;

import java.util.List;

public interface RoleService {
    BaseResponse createRole(CreateRoleRequest request, String authenticatedUser);

    Role updateRole(int id, Role role);

    void deleteRole(int id);

    List<Role> getAllRoles();

    BaseResponse assignPermissionsToRole(AssignAndUnassignRolesRequest request);
    BaseResponse unAssignPermissionsFromRole(AssignAndUnassignRolesRequest request);


}
