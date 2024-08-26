package com.EmployeeMgtSystem.EmployeeService.exceptions;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Schema(
//        name = "Error response",
//        description = "Response format for unsuccessful operations"
//)
public class ErrorDetails {
    private int statusCode;
    private boolean status;
    private String message;
}
