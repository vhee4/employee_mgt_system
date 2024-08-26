package com.EmployeeMgtSystem.AuthenticationServer.service.impl;

import com.EmployeeMgtSystem.AuthenticationServer.config.JwtConfig;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.ChangePasswordRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.LoginRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.UserResponse;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.LoginResponse;
import com.EmployeeMgtSystem.AuthenticationServer.enums.Status;
import com.EmployeeMgtSystem.AuthenticationServer.exceptions.ResourceNotFoundException;
import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import com.EmployeeMgtSystem.AuthenticationServer.model.User;
import com.EmployeeMgtSystem.AuthenticationServer.repository.RoleRepository;
import com.EmployeeMgtSystem.AuthenticationServer.repository.UserRepository;
import com.EmployeeMgtSystem.AuthenticationServer.service.AuthService;
import com.EmployeeMgtSystem.AuthenticationServer.validator.PasswordValidator;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private PasswordValidator passwordValidator;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtConfig jwtConfig, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public BaseResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(()-> new ResourceNotFoundException("User not found"));

            if (user == null) {
                throw  new BadRequestException("Invalid Login Credentials");
            }
            if(user.getStatus().equals(Status.PENDING)){
                throw  new BadRequestException("Cannot Login. Status: Pending");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            if(!authentication.isAuthenticated()){
                throw  new BadRequestException("Invalid Login Credentials");
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> permissions = extractPermissionsFromAuthorities(authorities);
            String token = jwtConfig.generateToken(authentication.getName(), authorities, permissions, user);

            LoginResponse response = new LoginResponse(token);
            return BaseResponse.getSuccessfulResponse("Login Successful", response);

        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid login credentials");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseResponse changePassword(ChangePasswordRequest request) {
        if (request.getEmail() == null ||request.getEmail().isEmpty() || request.getOldPassword() == null || request.getNewPassword() == null) {
            return BaseResponse.getResponse("Invalid input", null, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return BaseResponse.getResponse("Old password is incorrect", null, HttpStatus.UNAUTHORIZED);
        }

        boolean validPassword = passwordValidator.validate(request.getNewPassword());
        if(!validPassword){
            throw new BadRequestException("Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return BaseResponse.getResponse("Password changed successfully", null, HttpStatus.OK);
    }

    @Override
    @Transactional
    public BaseResponse createUser(CreateUserRequest request,Authentication authentication) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if(existingUser.isPresent()){
            throw  new BadRequestException("Email already exists");
        }
        Role role = roleRepository.findByNameIgnoreCase(request.getRole()).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        User user = User.builder()
                .email(request.getEmail())
                .roles(Collections.singleton(role))
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getFirstName() + " " + request.getLastName())
                .build();
        user.setStatus(Status.ACTIVE);
        user.setCreatedBy(authentication.getName());
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);

        UserResponse response = getCreateUserResponse(user, role);
        System.out.println("Response: "+response.toString());
        return BaseResponse.getResponse("User created Successfully",response, HttpStatus.CREATED);
    }



    private UserResponse getCreateUserResponse(User user, Role role) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .status(user.getStatus().toString())
                .createdBy(user.getCreatedBy())
                .createdTime(user.getCreatedTime())
                .roles(Collections.singleton(role.getName()))
                .build();
    }

    private List<String> extractPermissionsFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .flatMap(auth -> getPermissionsByRoleName(auth.getAuthority()).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getPermissionsByRoleName(String roleName) {
        Role role = roleRepository.findByNameIgnoreCase(roleName).orElseThrow();
        return role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }
}


