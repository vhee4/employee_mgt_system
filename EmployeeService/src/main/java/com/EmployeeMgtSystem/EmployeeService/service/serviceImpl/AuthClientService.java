package com.EmployeeMgtSystem.EmployeeService.service.serviceImpl;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateUserRequest;
import com.EmployeeMgtSystem.EmployeeService.dto.response.BaseResponse;
import com.EmployeeMgtSystem.EmployeeService.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthClientService {
    String baseUrl = "http://localhost:8082/api/v1/auth";
    private final WebClient webClient;

    @Autowired
    public AuthClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UserResponse> getUserByEmail(String email,String token) {

        return webClient.get()
                .uri(baseUrl+"/user?email={email}", email)
                .header("Authorization",token)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }

    public Mono<BaseResponse> createUser(CreateUserRequest request, String token) {

        return webClient.post()
                .uri(baseUrl + "/create-user")
                .header("Authorization", token)  // Include the JWT token in the Authorization header
                .bodyValue(request)  // Attach the request body
                .retrieve()
                .bodyToMono(BaseResponse.class);
    }

}
