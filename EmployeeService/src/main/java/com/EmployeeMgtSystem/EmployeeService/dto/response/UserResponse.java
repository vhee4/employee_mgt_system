package com.EmployeeMgtSystem.EmployeeService.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String status;
    private String createdBy;
    private String password;
    private String createdTime;
    private List<String> roles;
}
