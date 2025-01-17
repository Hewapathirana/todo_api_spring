package com.ntloc.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ApiErrorResponse handleCustomerNotFoundException(ResourceNotFoundException ex,
                                                            HttpServletRequest request,
                                                            HandlerMethod method) {

        return ApiErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .api(request.getMethod())
                .timestamp(ZonedDateTime.now())
                .build();

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Set the HTTP status
    public ApiErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ApiErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Validation failed for one or more fields")
                .path(request.getRequestURI())
                .api(request.getMethod())
                .timestamp(ZonedDateTime.now())
                .errors(errors)
                .build();
    }
}
