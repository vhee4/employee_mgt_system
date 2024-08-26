package com.EmployeeMgtSystem.AuthenticationServer.exceptions;

import jakarta.ws.rs.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();

        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach(objectError -> {
            String field = ((FieldError) objectError).getField();
            String errorMessage = objectError.getDefaultMessage();
            validationErrors.put(field, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(InputValidationDetails.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(false)
                .message("One or more validation errors occurred")
                .errors(validationErrors)
                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapToErrorDetails(exception, HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorDetails> handleInternalServerException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapToErrorDetails(exception, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorDetails> handleResourceAlreadyExistException(Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mapToErrorDetails(exception, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapToErrorDetails(exception, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapToErrorDetails(exception, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }


    private ErrorDetails mapToErrorDetails(Exception exception, int statusCode) {
        return ErrorDetails.builder()
                .statusCode(statusCode)
                .status(false)
                .message(exception.getMessage())
                .build();
    }
}
