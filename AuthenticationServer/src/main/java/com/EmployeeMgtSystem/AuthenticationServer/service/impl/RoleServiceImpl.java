package com.EmployeeMgtSystem.AuthenticationServer.service.impl;

import com.EmployeeMgtSystem.AuthenticationServer.dto.request.AssignAndUnassignRolesRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateRoleRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.enums.Status;
import com.EmployeeMgtSystem.AuthenticationServer.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.repository.PermissionRepository;
import com.EmployeeMgtSystem.AuthenticationServer.repository.RoleRepository;
import com.EmployeeMgtSystem.AuthenticationServer.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public BaseResponse createRole(CreateRoleRequest request, String authenticatedUser) {

        Optional<Role> optionalRole = roleRepository.findByNameIgnoreCase(request.getName());
        if (optionalRole.isPresent() && optionalRole.get().getStatus().equals(Status.ACTIVE)) {
            String message = "role with name: " + request.getName() + " already exists";
            return new BaseResponse(HttpStatus.OK, HttpStatus.OK.value(), message);
        }
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(new HashSet<>(permissions))
                .build();
        role.setCreatedBy(authenticatedUser);
        role.setCreatedTime(LocalDateTime.now());
        role.setStatus(Status.ACTIVE);
        roleRepository.save(role);
        String message = "role successfully created";
        return new BaseResponse(HttpStatus.CREATED, HttpStatus.CREATED.value(), message);
    }

    @Override
    public Role updateRole(int id, Role role) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(int id) {
        Role role  = roleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        role.setStatus(Status.DELETED);
        roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public BaseResponse assignPermissionsToRole(AssignAndUnassignRolesRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        role.setPermissions(new HashSet<>(permissions));
        //add checks for wrong permissions or already existing permissions
        roleRepository.save(role);
        String message = "permissions successfully assigned";
        return new BaseResponse(HttpStatus.OK, HttpStatus.OK.value(), message);
    }

    @Override
    public BaseResponse unAssignPermissionsFromRole(AssignAndUnassignRolesRequest request) {
        String message = "";
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if(!permissions.isEmpty()){
        permissions.forEach(role.getPermissions()::remove);
        roleRepository.save(role);
        message = "permissions successfully assigned";
        }
        return new BaseResponse(HttpStatus.OK, HttpStatus.OK.value(), message);
    }
}
