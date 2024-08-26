package com.EmployeeMgtSystem.EmployeeService.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;


@Data
@Builder
@NoArgsConstructor
@ToString
public class BaseResponse {
    @JsonIgnore
    private HttpStatus status;
    private int code;
    private String message;
    private Object data;

    public BaseResponse(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public BaseResponse(HttpStatus status, int code, String message, Object data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static BaseResponse getSuccessfulResponse(String message,Object data){
        return BaseResponse.builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static BaseResponse getResponse(String message,Object data,HttpStatus status){
        return BaseResponse.builder()
                .status(status)
                .code(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public static BaseResponse getFailureResponse(String message,Object data){
        return BaseResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .data(data)
                .build();
    }

}

