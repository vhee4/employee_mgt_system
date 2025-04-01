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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BaseResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user == null) {
                throw new BadRequestException("Invalid Login Credentials");
            }
            if (user.getStatus().equals(Status.PENDING)) {
                throw new BadRequestException("Cannot Login. Status: Pending");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            if (!authentication.isAuthenticated()) {
                throw new BadRequestException("Invalid Login Credentials");
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
        if (request.getEmail() == null || request.getEmail().isEmpty() || request.getOldPassword() == null || request.getNewPassword() == null) {
            return BaseResponse.getResponse("Invalid input", null, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return BaseResponse.getResponse("Old password is incorrect", null, HttpStatus.UNAUTHORIZED);
        }

        boolean validPassword = passwordValidator.validate(request.getNewPassword());
        if (!validPassword) {
            throw new BadRequestException("Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return BaseResponse.getResponse("Password changed successfully", null, HttpStatus.OK);
    }

    @Override
    @Transactional
    public BaseResponse createUser(CreateUserRequest request, Authentication authentication) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        Role role = roleRepository.findByNameIgnoreCase(request.getRole()).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        User user = User.builder()
                .email(request.getEmail())
                .roles(Collections.singleton(role))
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getFirstName() + " " + request.getLastName())
                .build();
        user.setStatus(Status.ACTIVE);
        user.setCreatedBy(authentication != null ? authentication.getName() : "Super Admin");
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);

        UserResponse response = getCreateUserResponse(user, role, request.getPassword());
        System.out.println("Response: " + response.toString());
        return BaseResponse.getResponse("User created Successfully", response, HttpStatus.CREATED);
    }

    @RabbitListener(queues = "employee.user.create.request.queue")
    public void handleEmployeeUserCreation(Message message) {
        try {
            System.out.println("Received Message: " + Arrays.toString(message.getBody()));

            // Deserialize message body
            String body = new String(message.getBody());
            System.out.println("request received: "+body);
            CreateUserRequest request = objectMapper.readValue(body, CreateUserRequest.class);

            System.out.println("create user request received: "+request);

            BaseResponse response = createUser(request, null);

            String jsonResponse = objectMapper.writeValueAsString(response); // Convert DTO to JSON
            System.out.println("sending message to auth service");

            rabbitTemplate.convertAndSend("user_creation_exchange", "user.create.response", jsonResponse);
            System.out.println("Published user creation response:");

        } catch (Exception e) {
            System.err.println("Failed to process employee user creation request: " + e.getMessage());
        }
    }

    public String convertLocalDateTimeToString(LocalDateTime createdTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return createdTime.format(formatter);
    }


    private UserResponse getCreateUserResponse(User user, Role role, String password) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .status(user.getStatus().toString())
                .createdBy(user.getCreatedBy())
                .createdTime(convertLocalDateTimeToString(user.getCreatedTime()))
                .roles(Collections.singleton(role.getName()))
                .password(password)
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


