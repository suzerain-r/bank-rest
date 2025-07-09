package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleNotFound(NotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionMessage> handleValidation(ValidationException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionMessage> handleAccessDenied() {
        return buildResponse("Доступ запрещён", HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ExceptionMessage> buildResponse(String message, HttpStatus status) {
        ExceptionMessage response = new ExceptionMessage(message);
        return new ResponseEntity<>(response, status);
    }
}
