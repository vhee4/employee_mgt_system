package com.EmployeeMgtSystem.AuthenticationServer.service.impl;

import com.EmployeeMgtSystem.AuthenticationServer.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.repository.PermissionRepository;
import com.EmployeeMgtSystem.AuthenticationServer.repository.RoleRepository;
import com.EmployeeMgtSystem.AuthenticationServer.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Role createRole(Role role) {
        //TODO: add a check to see if the role already exist
        return roleRepository.save(role);
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
        roleRepository.deleteById(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void assignPermissionsToRole(int roleId, List<Integer> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
    }
}
