package com.EmployeeMgtSystem.AuthenticationServer.service;

import com.EmployeeMgtSystem.AuthenticationServer.dto.request.ChangePasswordRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.AuthenticationServer.dto.response.BaseResponse;
import com.EmployeeMgtSystem.AuthenticationServer.dto.request.LoginRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    BaseResponse login(LoginRequest request);
    BaseResponse changePassword(ChangePasswordRequest request);
    BaseResponse createUser(CreateUserRequest request, Authentication authentication);
}
