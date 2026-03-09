package com.example.Refactoring;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map<String, String> handleException(Exception e) {

        return Map.of(
                "error", "Server error",
                "message", e.getMessage()
        );
    }
}