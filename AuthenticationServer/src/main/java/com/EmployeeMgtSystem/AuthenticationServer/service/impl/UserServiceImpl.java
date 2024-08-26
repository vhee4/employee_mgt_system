package com.EmployeeMgtSystem.AuthenticationServer.service.impl;

import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.model.User;
import com.EmployeeMgtSystem.AuthenticationServer.repository.RoleRepository;
import com.EmployeeMgtSystem.AuthenticationServer.repository.UserRepository;
import com.EmployeeMgtSystem.AuthenticationServer.service.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public String createUserWithDefaultRoles(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
//        user.setEmail();

        // Assign default role
        Role employeeRole = roleRepository.findByNameIgnoreCase("EMPLOYEE").orElseThrow();
        user.setRoles(Set.of(employeeRole));

        userRepository.save(user);
        return "success";
    }
    public String createAdminUser() {
        User user = new User();
        user.setUsername("chioma5");
        user.setPassword(passwordEncoder.encode("new"));
        user.setEmail("chi2@gmail.com");
        user.setFirstName("chi chi");
        user.setLastName("chi chi");
        user.setCreatedBy("me");
        user.setUpdatedBy("me");
//        user.setEmail();

        // Assign default role
        Role employeeRole = roleRepository.findByNameIgnoreCase("EMPLOYEE").orElseThrow();

        System.out.println("admin role"+employeeRole.toString());
        System.out.println("admin permissions"+employeeRole.getPermissions().toString());

//        Hibernate.initialize(employeeRole.getUsers());
        user.setRoles(Collections.singleton(employeeRole));
//        employeeRole.add(user);

        userRepository.save(user);
        return "Success";
    }
}
