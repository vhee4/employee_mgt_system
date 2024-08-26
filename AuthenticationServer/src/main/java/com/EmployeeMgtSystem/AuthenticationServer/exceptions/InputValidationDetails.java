package com.EmployeeMgtSystem.AuthenticationServer.exceptions;

import lombok.*;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InputValidationDetails {
    private int statusCode;
    private boolean status;
    private String message;
    private Map<String, String> errors;
}
