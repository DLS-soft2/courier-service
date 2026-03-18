package com.dls.courierservice.Configuration;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Ikke fundet
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(
                    HttpStatus.NOT_FOUND.value(),
                    ex.getMessage()
            ));
        }
        // Alle andre RuntimeExceptions → 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        ));
    }

    // 400 - Ugyldigt input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        ));
    }

    // 409 - Duplicate email eller anden constraint violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException ex) {
        String message = "A restaurant with this email already exists";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(
                HttpStatus.CONFLICT.value(),
                message
        ));
    }

    private Map<String, Object> errorBody(int status, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status,
                "message", message != null ? message : "An error occurred"
        );
    }
}