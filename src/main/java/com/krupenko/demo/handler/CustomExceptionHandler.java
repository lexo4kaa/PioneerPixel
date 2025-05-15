package com.krupenko.demo.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import static com.krupenko.demo.constansts.ExceptionMessages.INVALID_USERNAME_OR_PASSWORD;

@Log4j2
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("ResponseStatusException caught: status={}, reason={}", ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getReason());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindException(BindException ex) {
        String errorMessage = ex.getFieldErrors().stream()
                .map(fieldError -> {
                    Class<?> fieldType = getFieldType(ex, fieldError.getField());
                    if (fieldType != null && fieldType.equals(java.time.LocalDate.class)) {
                        return getErrorMessage(fieldError.getField(), "Expected format: yyyy-MM-dd");
                    }
                    return getErrorMessage(fieldError.getField(), fieldError.getDefaultMessage());
                })
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Invalid request parameters");
        log.debug("BindException details: {}", errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        log.warn("AuthenticationException caught: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_USERNAME_OR_PASSWORD);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error("Unexpected exception caught: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    private Class<?> getFieldType(BindException exception, String fieldName) {
        try {
            return exception.getTarget().getClass().getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private String getErrorMessage(String field, String message) {
        return String.format("Invalid value for field '%s'. %s", field, message);
    }

}
