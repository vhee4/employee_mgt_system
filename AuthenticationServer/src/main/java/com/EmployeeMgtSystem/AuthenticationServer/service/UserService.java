package com.EmployeeMgtSystem.AuthenticationServer.service;

public interface UserService {
    String createAdminUser();
    String createUserWithDefaultRoles(String username, String password);
}
