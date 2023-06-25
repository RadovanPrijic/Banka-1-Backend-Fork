package org.banka1.bankservice.controllers;

import org.banka1.bankservice.domains.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error: exception.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, String> handleBadRequestException(BadRequestException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Map<String, String> handleForbiddenException(ForbiddenException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerError.class)
    public Map<String, String> handleInternalServerError(InternalServerError exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidationException(ValidationException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

}
