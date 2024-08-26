package com.EmployeeMgtSystem.AuthenticationServer.service.impl;

import com.EmployeeMgtSystem.AuthenticationServer.dto.response.UserResponse;
import com.EmployeeMgtSystem.AuthenticationServer.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.model.User;
import com.EmployeeMgtSystem.AuthenticationServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmployeeClientService {
    @Autowired
    private UserRepository userRepository;

    public UserResponse getUserByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .status(user.getStatus().toString())
                .createdBy(user.getCreatedBy())
                .createdTime(user.getCreatedTime())
                .roles(Set.of(user.getRoles().stream().map(Role::getName).toString()))
                .build();
    }

}
